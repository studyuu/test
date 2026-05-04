package com.example.movieticket.controller;

import com.example.movieticket.entity.Cinema;
import com.example.movieticket.entity.Movie;
import com.example.movieticket.entity.Order;
import com.example.movieticket.entity.Schedule;
import com.example.movieticket.entity.Seat;
import com.example.movieticket.repository.CinemaRepository;
import com.example.movieticket.repository.MovieRepository;
import com.example.movieticket.repository.OrderRepository;
import com.example.movieticket.repository.ScheduleRepository;
import com.example.movieticket.repository.SeatRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private SeatRepository seatRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/orders")
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long scheduleId = Long.valueOf(request.get("scheduleId").toString());
            Long userId = Long.valueOf(request.get("userId").toString());
            List<Map<String, Object>> selectedSeats = (List<Map<String, Object>>) request.get("selectedSeats");

            Optional<Schedule> scheduleOpt = scheduleRepository.findById(scheduleId);
            if (!scheduleOpt.isPresent()) {
                response.put("code", 404);
                response.put("message", "排期不存在");
                response.put("data", null);
                return response;
            }

            Schedule schedule = scheduleOpt.get();
            Optional<Movie> movieOpt = movieRepository.findById(schedule.getMovieId());
            Optional<Cinema> cinemaOpt = cinemaRepository.findById(schedule.getCinemaId());

            if (!movieOpt.isPresent()) {
                response.put("code", 404);
                response.put("message", "影片不存在");
                response.put("data", null);
                return response;
            }

            Movie movie = movieOpt.get();
            Cinema cinema = cinemaOpt.isPresent() ? cinemaOpt.get() : null;

            String orderId = "ORD" + System.currentTimeMillis();
            Order order = new Order();
            order.setOrderId(orderId);
            order.setUserId(userId);
            order.setScheduleId(scheduleId);
            order.setSeats(objectMapper.writeValueAsString(selectedSeats));
            order.setTotalPrice(new BigDecimal(schedule.getPrice() * selectedSeats.size()));
            order.setStatus("pending");

            orderRepository.save(order);

            List<Seat> allSeats = seatRepository.findByScheduleId(scheduleId);
            for (Map<String, Object> seatData : selectedSeats) {
                Integer rowNum = Integer.valueOf(seatData.get("row").toString());
                Integer colNum = Integer.valueOf(seatData.get("col").toString());
                for (Seat seat : allSeats) {
                    if (seat.getRowNum().equals(rowNum) && seat.getColNum().equals(colNum)) {
                        seat.setStatus("sold");
                        seat.setOrderId(orderId);
                        seatRepository.save(seat);
                    }
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("orderId", orderId);
            result.put("movieName", movie.getTitle());
            result.put("cinemaName", schedule.getCinemaName());
            result.put("hallName", schedule.getHallName());
            result.put("startTime", schedule.getStartTime());
            result.put("selectedSeats", selectedSeats);
            result.put("totalPrice", order.getTotalPrice());
            result.put("status", order.getStatus());
            result.put("createTime", order.getCreateTime());

            response.put("code", 200);
            response.put("message", "创建订单成功");
            response.put("data", result);

        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "创建订单失败: " + e.getMessage());
            response.put("data", null);
        }

        return response;
    }

    @GetMapping("/orders/{orderId}")
    public Map<String, Object> getOrderDetail(@PathVariable String orderId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (!orderOpt.isPresent()) {
            response.put("code", 404);
            response.put("message", "订单不存在");
            response.put("data", null);
            return response;
        }

        Order order = orderOpt.get();
        Optional<Schedule> scheduleOpt = scheduleRepository.findById(order.getScheduleId());
        if (!scheduleOpt.isPresent()) {
            response.put("code", 404);
            response.put("message", "排期不存在");
            response.put("data", null);
            return response;
        }

        Schedule schedule = scheduleOpt.get();
        Optional<Movie> movieOpt = movieRepository.findById(schedule.getMovieId());
        Optional<Cinema> cinemaOpt = cinemaRepository.findById(schedule.getCinemaId());

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getOrderId());
        result.put("userId", order.getUserId());
        result.put("scheduleId", order.getScheduleId());
        result.put("movieId", schedule.getMovieId());
        result.put("movieName", movieOpt.isPresent() ? movieOpt.get().getTitle() : null);
        result.put("cinemaId", schedule.getCinemaId());
        result.put("cinemaName", schedule.getCinemaName());
        result.put("hallName", schedule.getHallName());
        result.put("startTime", schedule.getStartTime());
        result.put("endTime", schedule.getEndTime());
        result.put("totalPrice", order.getTotalPrice());
        result.put("status", order.getStatus());
        result.put("createTime", order.getCreateTime());
        result.put("updateTime", order.getUpdateTime());

        try {
            List<Map<String, Object>> selectedSeats = objectMapper.readValue(order.getSeats(), List.class);
            result.put("selectedSeats", selectedSeats);
        } catch (Exception e) {
            result.put("selectedSeats", new ArrayList<>());
        }

        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);

        return response;
    }

    @GetMapping("/users/{userId}/orders")
    public Map<String, Object> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "all") String status) {

        Map<String, Object> response = new HashMap<>();

        List<Order> orders = orderRepository.findByUserIdOrderByCreateTimeDesc(userId);

        int total = orders.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        List<Order> pageOrders = start < total ? orders.subList(start, end) : new ArrayList<>();

        List<Map<String, Object>> orderList = new ArrayList<>();
        for (Order order : pageOrders) {
            Map<String, Object> orderItem = new HashMap<>();
            orderItem.put("orderId", order.getOrderId());

            Optional<Schedule> scheduleOpt = scheduleRepository.findById(order.getScheduleId());
            if (scheduleOpt.isPresent()) {
                Schedule schedule = scheduleOpt.get();
                orderItem.put("cinemaName", schedule.getCinemaName());
                orderItem.put("hallName", schedule.getHallName());
                orderItem.put("showTime", schedule.getStartTime());

                Optional<Movie> movieOpt = movieRepository.findById(schedule.getMovieId());
                if (movieOpt.isPresent()) {
                    Movie movie = movieOpt.get();
                    orderItem.put("movieId", movie.getMovieId());
                    orderItem.put("movieTitle", movie.getTitle());
                    orderItem.put("poster", movie.getPoster());
                }
            }

            orderItem.put("seats", order.getSeats());
            orderItem.put("totalPrice", order.getTotalPrice());

            String orderStatus = getOrderStatusWithTimeout(order);

            if (!"all".equals(status)) {
                String statusCode = "pending";
                if ("completed".equals(status)) {
                    statusCode = "completed";
                } else if ("cancelled".equals(status)) {
                    statusCode = "cancelled";
                }
                String originalStatus = order.getStatus();
                if ("pending".equals(originalStatus) && isOrderExpired(order)) {
                    originalStatus = "cancelled";
                }
                if (!statusCode.equals(originalStatus)) {
                    continue;
                }
            }

            orderItem.put("status", orderStatus);

            if (order.getCreateTime() != null) {
                orderItem.put("createTime",
                        order.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            }

            orderList.add(orderItem);
        }

        Map<String, Object> data = new HashMap<>();
        data.put("list", orderList);
        data.put("total", orderList.size());

        response.put("code", 200);
        response.put("message", "success");
        response.put("data", data);

        return response;
    }

    private boolean isOrderExpired(Order order) {
        if ("pending".equals(order.getStatus()) && order.getCreateTime() != null) {
            LocalDateTime expireTime = order.getCreateTime().plusMinutes(10);
            return LocalDateTime.now().isAfter(expireTime);
        }
        return false;
    }

    private String getOrderStatusWithTimeout(Order order) {
        String orderStatus = order.getStatus();
        if ("pending".equals(orderStatus)) {
            if (isOrderExpired(order)) {
                orderStatus = "已取消";
            } else {
                orderStatus = "待支付";
            }
        } else if ("completed".equals(orderStatus)) {
            orderStatus = "已完成";
        } else if ("cancelled".equals(orderStatus)) {
            orderStatus = "已取消";
        }
        return orderStatus;
    }

    @GetMapping("/orders/{orderId}/detail")
    public Map<String, Object> getOrderDetailForUser(@PathVariable String orderId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (!orderOpt.isPresent()) {
            response.put("code", 404);
            response.put("message", "订单不存在");
            response.put("data", null);
            return response;
        }

        Order order = orderOpt.get();
        Optional<Schedule> scheduleOpt = scheduleRepository.findById(order.getScheduleId());
        if (!scheduleOpt.isPresent()) {
            response.put("code", 404);
            response.put("message", "排期不存在");
            response.put("data", null);
            return response;
        }

        Schedule schedule = scheduleOpt.get();
        Optional<Movie> movieOpt = movieRepository.findById(schedule.getMovieId());

        Map<String, Object> result = new HashMap<>();
        result.put("orderId", order.getOrderId());

        if (movieOpt.isPresent()) {
            Movie movie = movieOpt.get();
            result.put("movieTitle", movie.getTitle());
            result.put("poster", movie.getPoster());
        }

        result.put("cinemaName", schedule.getCinemaName());
        result.put("hallName", schedule.getHallName());
        result.put("showTime", schedule.getStartTime());
        result.put("endTime", schedule.getEndTime());
        result.put("seats", order.getSeats());
        result.put("totalPrice", order.getTotalPrice());

        String orderStatus = getOrderStatusWithTimeout(order);
        result.put("status", orderStatus);

        if (order.getCreateTime() != null) {
            result.put("createTime", order.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        String ticketCode = "QR" + orderId.substring(3);
        result.put("ticketCode", ticketCode);

        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);

        return response;
    }

    @PostMapping("/orders/{orderId}/refresh")
    public Map<String, Object> refreshOrderStatus(@PathVariable String orderId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (!orderOpt.isPresent()) {
            response.put("code", 404);
            response.put("message", "订单不存在");
            response.put("data", null);
            return response;
        }

        Order order = orderOpt.get();

        if ("pending".equals(order.getStatus())) {
            order.setStatus("completed");
            orderRepository.save(order);
            response.put("code", 200);
            response.put("message", "订单状态已更新为已支付");
            response.put("data", "completed");
        } else {
            response.put("code", 200);
            response.put("message", "订单状态已是" + order.getStatus());
            response.put("data", order.getStatus());
        }

        return response;
    }
}
