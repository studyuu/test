package com.example.movieticket.controller;

import com.example.movieticket.entity.*;
import com.example.movieticket.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cinema-admin")
public class CinemaAdminController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private HallRepository hallRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long getCinemaIdFromUser(Long userId) {
        Optional<SysUser> userOpt = sysUserRepository.findById(userId);
        return userOpt.map(SysUser::getCinemaId).orElse(null);
    }

    @GetMapping("/dashboard")
    public Map<String, Object> getDashboard(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        Long cinemaId = getCinemaIdFromUser(userId);

        if (cinemaId == null) {
            result.put("code", 400);
            result.put("message", "用户未关联影院");
            return result;
        }

        Map<String, Object> data = new HashMap<>();

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        Double todayRevenue = orderRepository.sumTotalPriceByCinemaIdAndCreateTimeBetween(cinemaId, startOfDay,
                endOfDay);
        data.put("todayRevenue", todayRevenue != null ? todayRevenue : 0.0);

        List<Order> cinemaOrders = orderRepository.findByCinemaId(cinemaId);
        long pendingOrders = cinemaOrders.stream()
                .filter(order -> "completed".equals(order.getStatus()))
                .count();
        data.put("pendingOrders", (int) pendingOrders);

        Double monthRevenue = orderRepository.sumTotalPriceByCinemaIdAndCreateTimeBetween(
                cinemaId, today.withDayOfMonth(1).atStartOfDay(), endOfDay);
        data.put("monthRevenue", monthRevenue != null ? monthRevenue : 0.0);

        Optional<Cinema> cinemaOpt = cinemaRepository.findById(cinemaId);
        if (cinemaOpt.isPresent()) {
            data.put("rating", cinemaOpt.get().getRating() != null ? cinemaOpt.get().getRating() : 0);
        }

        List<Map<String, Object>> recentOrders = cinemaOrders.stream()
                .limit(10)
                .map(order -> {
                    Map<String, Object> orderMap = new HashMap<>();
                    orderMap.put("orderNo", order.getOrderId());
                    orderMap.put("totalAmount", order.getTotalPrice());
                    orderMap.put("status", getOrderStatusText(order));
                    if (order.getCreateTime() != null) {
                        orderMap.put("createTime",
                                order.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    }

                    Optional<Schedule> scheduleOpt = scheduleRepository.findById(order.getScheduleId());
                    if (scheduleOpt.isPresent()) {
                        Schedule schedule = scheduleOpt.get();
                        orderMap.put("hallName", schedule.getHallName());
                        orderMap.put("showTime", schedule.getStartTime());

                        Optional<Movie> movieOpt = movieRepository.findById(schedule.getMovieId());
                        if (movieOpt.isPresent()) {
                            orderMap.put("movieName", movieOpt.get().getTitle());
                        }
                    }
                    return orderMap;
                })
                .collect(Collectors.toList());
        data.put("recentOrders", recentOrders);

        Map<String, Double> movieSales = new HashMap<>();
        cinemaOrders.forEach(order -> {
            Optional<Schedule> scheduleOpt = scheduleRepository.findById(order.getScheduleId());
            scheduleOpt.ifPresent(schedule -> {
                Optional<Movie> movieOpt = movieRepository.findById(schedule.getMovieId());
                movieOpt.ifPresent(movie -> {
                    String movieName = movie.getTitle();
                    Double price = order.getTotalPrice() != null ? order.getTotalPrice().doubleValue() : 0.0;
                    movieSales.merge(movieName, price, Double::sum);
                });
            });
        });

        List<Map<String, Object>> topMovies = movieSales.entrySet().stream()
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(entry -> {
                    Map<String, Object> movieMap = new HashMap<>();
                    movieMap.put("movieName", entry.getKey());
                    movieMap.put("sales", entry.getValue());
                    return movieMap;
                })
                .collect(Collectors.toList());
        data.put("topMovies", topMovies);

        List<Schedule> todayScheduleList = scheduleRepository.findByCinemaId(cinemaId).stream()
                .filter(schedule -> {
                    if (schedule.getStartTime() == null)
                        return false;
                    String datePart = schedule.getStartTime().split(" ")[0];
                    return datePart.equals(today.toString());
                })
                .collect(Collectors.toList());

        List<Map<String, Object>> todaySchedules = todayScheduleList.stream()
                .map(schedule -> {
                    Map<String, Object> scheduleMap = new HashMap<>();
                    scheduleMap.put("scheduleId", schedule.getId());
                    scheduleMap.put("hallName", schedule.getHallName());
                    scheduleMap.put("startTime", schedule.getStartTime());
                    scheduleMap.put("endTime", schedule.getEndTime());
                    scheduleMap.put("price", schedule.getPrice());
                    scheduleMap.put("status", schedule.getStatus());

                    Optional<Movie> movieOpt = movieRepository.findById(schedule.getMovieId());
                    movieOpt.ifPresent(movie -> scheduleMap.put("movieName", movie.getTitle()));

                    // 计算已售座位和总座位
                    int soldSeats = 0;
                    int totalSeats = 0;

                    Optional<Hall> hallOpt = hallRepository.findById(schedule.getHallId());
                    if (hallOpt.isPresent()) {
                        Hall hall = hallOpt.get();
                        totalSeats = hall.getRowCount() * hall.getColCount();
                    }

                    List<Order> scheduleOrders = orderRepository.findByScheduleId(schedule.getId());
                    for (Order order : scheduleOrders) {
                        if (order.getStatus() != null && !"cancelled".equals(order.getStatus())
                                && !"refunded".equals(order.getStatus())) {
                            try {
                                List<Map<String, Object>> seatList = objectMapper.readValue(order.getSeats(),
                                        List.class);
                                soldSeats += seatList.size();
                            } catch (Exception e) {
                                // 忽略解析错误
                            }
                        }
                    }

                    scheduleMap.put("soldSeats", soldSeats);
                    scheduleMap.put("totalSeats", totalSeats);

                    return scheduleMap;
                })
                .collect(Collectors.toList());
        data.put("todaySchedules", todaySchedules);

        Map<String, Object> stats = new HashMap<>();
        stats.put("todayRevenue", data.get("todayRevenue"));
        stats.put("pendingOrders", data.get("pendingOrders"));
        stats.put("monthRevenue", data.get("monthRevenue"));
        stats.put("rating", data.get("rating"));
        data.put("stats", stats);

        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);

        return result;
    }

    private String getOrderStatusText(Order order) {
        String status = order.getStatus();
        if ("pending".equals(status) && isOrderExpired(order)) {
            return "已取消";
        }
        if ("pending".equals(status))
            return "待支付";
        if ("completed".equals(status))
            return "已完成";
        if ("cancelled".equals(status))
            return "已取消";
        if ("refunding".equals(status))
            return "退票中";
        if ("refunded".equals(status))
            return "已退票";
        return status;
    }

    private boolean isOrderExpired(Order order) {
        if (order.getCreateTime() == null) {
            return false;
        }
        LocalDateTime expireTime = order.getCreateTime().plusMinutes(10);
        return LocalDateTime.now().isAfter(expireTime);
    }

    @GetMapping("/halls")
    public Map<String, Object> getHalls(@RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        Long cinemaId = getCinemaIdFromUser(userId);

        if (cinemaId == null) {
            result.put("code", 400);
            result.put("message", "用户未关联影院");
            return result;
        }

        List<Hall> halls = hallRepository.findByCinemaIdAndIsDeleted(cinemaId, 0);
        int total = halls.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        List<Hall> pageHalls = start < total ? halls.subList(start, end) : new ArrayList<>();

        List<Map<String, Object>> hallList = pageHalls.stream()
                .map(hall -> {
                    Map<String, Object> hallMap = new HashMap<>();
                    hallMap.put("hallId", hall.getHallId());
                    hallMap.put("hallName", hall.getHallName());
                    hallMap.put("rowCount", hall.getRowCount());
                    hallMap.put("colCount", hall.getColCount());
                    hallMap.put("status", hall.getStatus());
                    if (hall.getCreateTime() != null) {
                        hallMap.put("createTime",
                                hall.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    }
                    return hallMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("list", hallList);
        data.put("total", total);

        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);

        return result;
    }

    @PostMapping("/halls")
    public Map<String, Object> addHall(@RequestParam Long userId, @RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        Long cinemaId = getCinemaIdFromUser(userId);

        if (cinemaId == null) {
            result.put("code", 400);
            result.put("message", "用户未关联影院");
            return result;
        }

        try {
            Hall hall = new Hall();
            hall.setCinemaId(cinemaId);
            hall.setHallName(request.get("hallName").toString());
            hall.setRowCount(Integer.parseInt(request.get("rowCount").toString()));
            hall.setColCount(Integer.parseInt(request.get("colCount").toString()));
            hall.setStatus(request.get("status") != null ? Integer.parseInt(request.get("status").toString()) : 1);
            hall.setIsDeleted(0);

            hallRepository.save(hall);

            result.put("code", 200);
            result.put("message", "添加成功");
            result.put("data", hall.getHallId());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "添加失败: " + e.getMessage());
        }

        return result;
    }

    @PutMapping("/halls/{hallId}")
    public Map<String, Object> updateHall(@PathVariable Long hallId, @RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();

        Optional<Hall> hallOpt = hallRepository.findById(hallId);
        if (!hallOpt.isPresent()) {
            result.put("code", 404);
            result.put("message", "放映厅不存在");
            return result;
        }

        try {
            Hall hall = hallOpt.get();
            if (request.containsKey("hallName")) {
                hall.setHallName(request.get("hallName").toString());
            }
            if (request.containsKey("rowCount")) {
                hall.setRowCount(Integer.parseInt(request.get("rowCount").toString()));
            }
            if (request.containsKey("colCount")) {
                hall.setColCount(Integer.parseInt(request.get("colCount").toString()));
            }
            if (request.containsKey("status")) {
                hall.setStatus(Integer.parseInt(request.get("status").toString()));
            }

            hallRepository.save(hall);

            result.put("code", 200);
            result.put("message", "更新成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "更新失败: " + e.getMessage());
        }

        return result;
    }

    @DeleteMapping("/halls/{hallId}")
    public Map<String, Object> deleteHall(@PathVariable Long hallId) {
        Map<String, Object> result = new HashMap<>();

        Optional<Hall> hallOpt = hallRepository.findById(hallId);
        if (!hallOpt.isPresent()) {
            result.put("code", 404);
            result.put("message", "放映厅不存在");
            return result;
        }

        long scheduleCount = scheduleRepository.countByHallId(hallId);
        if (scheduleCount > 0) {
            result.put("code", 400);
            result.put("message", "该放映厅存在排期，无法删除");
            return result;
        }

        try {
            Hall hall = hallOpt.get();
            hall.setIsDeleted(1);
            hallRepository.save(hall);

            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败: " + e.getMessage());
        }

        return result;
    }

    @GetMapping("/schedules")
    public Map<String, Object> getSchedules(@RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();
        Long cinemaId = getCinemaIdFromUser(userId);

        if (cinemaId == null) {
            result.put("code", 400);
            result.put("message", "用户未关联影院");
            return result;
        }

        List<Schedule> schedules = scheduleRepository.findByCinemaId(cinemaId);
        int total = schedules.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        List<Schedule> pageSchedules = start < total ? schedules.subList(start, end) : new ArrayList<>();

        List<Map<String, Object>> scheduleList = pageSchedules.stream()
                .map(schedule -> {
                    Map<String, Object> scheduleMap = new HashMap<>();
                    scheduleMap.put("scheduleId", schedule.getId());
                    scheduleMap.put("movieId", schedule.getMovieId());
                    scheduleMap.put("hallId", schedule.getHallId());
                    scheduleMap.put("hallName", schedule.getHallName());
                    scheduleMap.put("startTime", schedule.getStartTime());
                    scheduleMap.put("endTime", schedule.getEndTime());
                    scheduleMap.put("price", schedule.getPrice());
                    scheduleMap.put("status", schedule.getStatus());

                    Optional<Movie> movieOpt = movieRepository.findById(schedule.getMovieId());
                    movieOpt.ifPresent(movie -> scheduleMap.put("movieName", movie.getTitle()));

                    long soldSeats = orderRepository.countByScheduleId(schedule.getId());
                    scheduleMap.put("soldSeats", (int) soldSeats);

                    return scheduleMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("list", scheduleList);
        data.put("total", total);

        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);

        return result;
    }

    @PostMapping("/schedules")
    public Map<String, Object> addSchedule(@RequestParam Long userId, @RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        Long cinemaId = getCinemaIdFromUser(userId);

        if (cinemaId == null) {
            result.put("code", 400);
            result.put("message", "用户未关联影院");
            return result;
        }

        try {
            Schedule schedule = new Schedule();
            schedule.setMovieId(Long.parseLong(request.get("movieId").toString()));
            schedule.setHallId(Long.parseLong(request.get("hallId").toString()));
            schedule.setCinemaId(cinemaId);

            Optional<Hall> hallOpt = hallRepository.findById(schedule.getHallId());
            if (hallOpt.isPresent()) {
                schedule.setHallName(hallOpt.get().getHallName());
            } else {
                result.put("code", 400);
                result.put("message", "放映厅不存在");
                return result;
            }

            Optional<Cinema> cinemaOpt = cinemaRepository.findById(cinemaId);
            cinemaOpt.ifPresent(c -> schedule.setCinemaName(cinemaOpt.get().getCinemaName()));

            schedule.setStartTime(request.get("startTime").toString());
            schedule.setEndTime(request.get("endTime").toString());
            schedule.setPrice(Double.parseDouble(request.get("price").toString()));
            schedule.setStatus(request.get("status") != null ? Integer.parseInt(request.get("status").toString()) : 1);
            schedule.setIsDeleted(0);

            scheduleRepository.save(schedule);

            result.put("code", 200);
            result.put("message", "添加成功");
            result.put("data", schedule.getId());
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "添加失败: " + e.getMessage());
        }

        return result;
    }

    @PutMapping("/schedules/{scheduleId}")
    public Map<String, Object> updateSchedule(@PathVariable Long scheduleId, @RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();

        Optional<Schedule> scheduleOpt = scheduleRepository.findById(scheduleId);
        if (!scheduleOpt.isPresent()) {
            result.put("code", 404);
            result.put("message", "排期不存在");
            return result;
        }

        try {
            Schedule schedule = scheduleOpt.get();
            if (request.containsKey("movieId")) {
                schedule.setMovieId(Long.parseLong(request.get("movieId").toString()));
            }
            if (request.containsKey("hallId")) {
                schedule.setHallId(Long.parseLong(request.get("hallId").toString()));
                Optional<Hall> hallOpt = hallRepository.findById(schedule.getHallId());
                hallOpt.ifPresent(hall -> schedule.setHallName(hall.getHallName()));
            }
            if (request.containsKey("startTime")) {
                schedule.setStartTime(request.get("startTime").toString());
            }
            if (request.containsKey("endTime")) {
                schedule.setEndTime(request.get("endTime").toString());
            }
            if (request.containsKey("price")) {
                schedule.setPrice(Double.parseDouble(request.get("price").toString()));
            }
            if (request.containsKey("status")) {
                schedule.setStatus(Integer.parseInt(request.get("status").toString()));
            }

            scheduleRepository.save(schedule);

            result.put("code", 200);
            result.put("message", "更新成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "更新失败: " + e.getMessage());
        }

        return result;
    }

    @DeleteMapping("/schedules/{scheduleId}")
    public Map<String, Object> deleteSchedule(@PathVariable Long scheduleId) {
        Map<String, Object> result = new HashMap<>();

        Optional<Schedule> scheduleOpt = scheduleRepository.findById(scheduleId);
        if (!scheduleOpt.isPresent()) {
            result.put("code", 404);
            result.put("message", "排期不存在");
            return result;
        }

        long orderCount = orderRepository.countByScheduleId(scheduleId);
        if (orderCount > 0) {
            result.put("code", 400);
            result.put("message", "该排期存在订单，无法删除");
            return result;
        }

        try {
            scheduleRepository.deleteById(scheduleId);
            result.put("code", 200);
            result.put("message", "删除成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "删除失败: " + e.getMessage());
        }

        return result;
    }

    @GetMapping("/orders")
    public Map<String, Object> getOrders(@RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "all") String status) {
        Map<String, Object> result = new HashMap<>();
        Long cinemaId = getCinemaIdFromUser(userId);

        if (cinemaId == null) {
            result.put("code", 400);
            result.put("message", "用户未关联影院");
            return result;
        }

        List<Order> orders = orderRepository.findByCinemaId(cinemaId);

        if (!"all".equals(status)) {
            orders = orders.stream()
                    .filter(order -> status.equals(order.getStatus()))
                    .collect(Collectors.toList());
        }

        int total = orders.size();
        int start = (pageNum - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        List<Order> pageOrders = start < total ? orders.subList(start, end) : new ArrayList<>();

        List<Map<String, Object>> orderList = pageOrders.stream()
                .map(order -> {
                    Map<String, Object> orderMap = new HashMap<>();
                    orderMap.put("orderId", order.getOrderId());
                    orderMap.put("orderNo", order.getOrderId());
                    orderMap.put("totalAmount", order.getTotalPrice());
                    orderMap.put("status", getOrderStatusText(order));
                    orderMap.put("seats", order.getSeats());

                    if (order.getCreateTime() != null) {
                        orderMap.put("createTime",
                                order.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    }

                    Optional<SysUser> userOpt = sysUserRepository.findById(order.getUserId());
                    if (userOpt.isPresent()) {
                        orderMap.put("userName", userOpt.get().getNickname() != null ? userOpt.get().getNickname()
                                : userOpt.get().getUsername());
                    }

                    Optional<Schedule> scheduleOpt = scheduleRepository.findById(order.getScheduleId());
                    if (scheduleOpt.isPresent()) {
                        Schedule schedule = scheduleOpt.get();
                        orderMap.put("hallName", schedule.getHallName());
                        orderMap.put("showTime", schedule.getStartTime());

                        Optional<Movie> movieOpt = movieRepository.findById(schedule.getMovieId());
                        movieOpt.ifPresent(movie -> orderMap.put("movieName", movie.getTitle()));
                    }

                    return orderMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("list", orderList);
        data.put("total", total);

        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);

        return result;
    }

    @PostMapping("/orders/handle-refund")
    public Map<String, Object> handleRefund(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();

        String orderId = request.get("orderId") != null ? request.get("orderId").toString() : null;
        String refundResult = request.get("result") != null ? request.get("result").toString() : null;
        String remark = request.get("remark") != null ? request.get("remark").toString() : "";

        if (orderId == null || refundResult == null) {
            result.put("code", 400);
            result.put("message", "参数错误");
            return result;
        }

        Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
        if (!orderOpt.isPresent()) {
            result.put("code", 404);
            result.put("message", "订单不存在");
            return result;
        }

        Order order = orderOpt.get();
        if (!"refunding".equals(order.getStatus())) {
            result.put("code", 400);
            result.put("message", "订单状态不正确，不是退票中状态");
            return result;
        }

        try {
            if ("approve".equals(refundResult)) {
                order.setStatus("refunded");
                order.setRefundReason(remark);
                order.setRefundStatus("approved");
                orderRepository.save(order);
                result.put("code", 200);
                result.put("message", "退票成功");
            } else if ("reject".equals(refundResult)) {
                order.setStatus("completed");
                order.setRefundReason(remark);
                order.setRefundStatus("rejected");
                orderRepository.save(order);
                result.put("code", 200);
                result.put("message", "已拒绝退票");
            } else {
                result.put("code", 400);
                result.put("message", "无效的处理结果");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "处理失败: " + e.getMessage());
        }

        return result;
    }

    @PostMapping("/orders/verify")
    public Map<String, Object> verifyTicket(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();

        String verifyCode = request.get("verifyCode") != null ? request.get("verifyCode").toString() : null;
        String orderId = request.get("orderId") != null ? request.get("orderId").toString() : null;

        Order order = null;
        if (verifyCode != null) {
            String orderNo = "ORD" + verifyCode.substring(2);
            Optional<Order> orderOpt = orderRepository.findByOrderId(orderNo);
            if (orderOpt.isPresent()) {
                order = orderOpt.get();
            }
        } else if (orderId != null) {
            Optional<Order> orderOpt = orderRepository.findByOrderId(orderId);
            if (orderOpt.isPresent()) {
                order = orderOpt.get();
            }
        }

        if (order == null) {
            result.put("code", 404);
            result.put("message", "订单不存在");
            return result;
        }

        if (!"completed".equals(order.getStatus())) {
            result.put("code", 400);
            result.put("message", "订单状态不正确，无法验票");
            return result;
        }

        order.setStatus("completed");
        orderRepository.save(order);

        result.put("code", 200);
        result.put("message", "验票成功");
        result.put("data", order.getOrderId());

        return result;
    }

    @GetMapping("/profile")
    public Map<String, Object> getProfile(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        Long cinemaId = getCinemaIdFromUser(userId);

        if (cinemaId == null) {
            result.put("code", 400);
            result.put("message", "用户未关联影院");
            return result;
        }

        Optional<Cinema> cinemaOpt = cinemaRepository.findById(cinemaId);
        if (!cinemaOpt.isPresent()) {
            result.put("code", 404);
            result.put("message", "影院不存在");
            return result;
        }

        Cinema cinema = cinemaOpt.get();
        Map<String, Object> data = new HashMap<>();
        data.put("cinemaId", cinema.getCinemaId());
        data.put("cinemaName", cinema.getCinemaName());
        data.put("address", cinema.getAddress());
        data.put("phone", cinema.getPhone());
        data.put("description", cinema.getDescription());
        data.put("logo", cinema.getLogo());
        data.put("status", cinema.getStatus());

        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);

        return result;
    }

    @PutMapping("/profile")
    public Map<String, Object> updateProfile(@RequestParam Long userId, @RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        Long cinemaId = getCinemaIdFromUser(userId);

        if (cinemaId == null) {
            result.put("code", 400);
            result.put("message", "用户未关联影院");
            return result;
        }

        Optional<Cinema> cinemaOpt = cinemaRepository.findById(cinemaId);
        if (!cinemaOpt.isPresent()) {
            result.put("code", 404);
            result.put("message", "影院不存在");
            return result;
        }

        try {
            Cinema cinema = cinemaOpt.get();
            if (request.containsKey("cinemaName")) {
                cinema.setCinemaName(request.get("cinemaName").toString());
            }
            if (request.containsKey("address")) {
                cinema.setAddress(request.get("address").toString());
            }
            if (request.containsKey("phone")) {
                cinema.setPhone(request.get("phone").toString());
            }
            if (request.containsKey("description")) {
                cinema.setDescription(request.get("description").toString());
            }
            if (request.containsKey("logo")) {
                cinema.setLogo(request.get("logo").toString());
            }
            if (request.containsKey("status")) {
                cinema.setStatus(Integer.parseInt(request.get("status").toString()));
            }

            cinemaRepository.save(cinema);

            result.put("code", 200);
            result.put("message", "保存成功");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "保存失败: " + e.getMessage());
        }

        return result;
    }
}