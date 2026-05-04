package com.example.movieticket.controller;

import com.example.movieticket.entity.Order;
import com.example.movieticket.entity.Movie;
import com.example.movieticket.entity.Schedule;
import com.example.movieticket.entity.SysUser;
import com.example.movieticket.repository.OrderRepository;
import com.example.movieticket.repository.MovieRepository;
import com.example.movieticket.repository.ScheduleRepository;
import com.example.movieticket.repository.SysUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @GetMapping("/stats")
    public Map<String, Object> getStats() {
        Map<String, Object> result = new HashMap<>();
        
        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        long todayOrders = orderRepository.countByCreateTimeBetween(startOfDay, endOfDay);
        
        Double todayRevenue = orderRepository.sumTotalPriceByCreateTimeBetween(startOfDay, endOfDay);
        if (todayRevenue == null) {
            todayRevenue = 0.0;
        }

        long totalUsers = sysUserRepository.count();
        long totalMovies = movieRepository.count();

        Map<String, Object> data = new HashMap<>();
        data.put("todayOrders", (int) todayOrders);
        data.put("todayRevenue", todayRevenue);
        data.put("totalUsers", (int) totalUsers);
        data.put("totalMovies", (int) totalMovies);

        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);

        return result;
    }

    @GetMapping("/order-trend")
    public Map<String, Object> getOrderTrend() {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> trendData = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(6);

        for (int i = 0; i < 7; i++) {
            LocalDate date = startDate.plusDays(i);
            LocalDateTime startOfDay = date.atStartOfDay();
            LocalDateTime endOfDay = date.atTime(23, 59, 59);

            long count = orderRepository.countByCreateTimeBetween(startOfDay, endOfDay);
            Double revenue = orderRepository.sumTotalPriceByCreateTimeBetween(startOfDay, endOfDay);
            if (revenue == null) {
                revenue = 0.0;
            }

            Map<String, Object> dayData = new HashMap<>();
            String dayName = getDayOfWeekName(date.getDayOfWeek().getValue());
            dayData.put("day", dayName);
            dayData.put("count", (int) count);
            dayData.put("revenue", revenue);
            trendData.add(dayData);
        }

        result.put("code", 200);
        result.put("message", "success");
        result.put("data", trendData);

        return result;
    }

    private String getDayOfWeekName(int dayOfWeek) {
        String[] weekDays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
        return weekDays[dayOfWeek - 1];
    }

    @GetMapping("/movie-type-distribution")
    public Map<String, Object> getMovieTypeDistribution() {
        Map<String, Object> result = new HashMap<>();

        List<Movie> movies = movieRepository.findAll();
        
        Map<String, Long> typeCount = movies.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getCategory() != null ? m.getCategory() : "其他",
                        Collectors.counting()
                ));

        long total = movies.size();
        List<Map<String, Object>> distribution = new ArrayList<>();

        typeCount.forEach((type, count) -> {
            Map<String, Object> item = new HashMap<>();
            item.put("type", type);
            item.put("count", count);
            item.put("percentage", total > 0 ? (int) (count * 100 / total) : 0);
            distribution.add(item);
        });

        distribution.sort((a, b) -> Long.compare((Long) b.get("count"), (Long) a.get("count")));

        result.put("code", 200);
        result.put("message", "success");
        result.put("data", distribution);

        return result;
    }

    @GetMapping("/recent-orders")
    public Map<String, Object> getRecentOrders(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Map<String, Object> result = new HashMap<>();

        Pageable pageable = PageRequest.of(pageNum - 1, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Order> orderPage = orderRepository.findAll(pageable);
        
        List<Map<String, Object>> orderList = orderPage.getContent().stream()
                .map(order -> {
                    Map<String, Object> orderMap = new HashMap<>();
                    orderMap.put("id", order.getId());
                    orderMap.put("orderId", order.getOrderId());
                    orderMap.put("seats", order.getSeats());
                    orderMap.put("totalPrice", order.getTotalPrice());
                    String status = order.getStatus();
                    if ("pending".equals(status)) {
                        status = "待支付";
                    } else if ("completed".equals(status)) {
                        status = "已完成";
                    } else if ("cancelled".equals(status)) {
                        status = "已取消";
                    }
                    orderMap.put("status", status);
                    if (order.getCreateTime() != null) {
                        orderMap.put("createTime", order.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    }
                    return orderMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> data = new HashMap<>();
        data.put("list", orderList);
        data.put("total", orderPage.getTotalElements());

        result.put("code", 200);
        result.put("message", "success");
        result.put("data", data);

        return result;
    }

    @GetMapping("/order-detail")
    public Map<String, Object> getOrderDetail(@RequestParam String orderId) {
        Map<String, Object> result = new HashMap<>();

        Order order = orderRepository.findByOrderId(orderId).orElse(null);
        if (order == null) {
            result.put("code", 404);
            result.put("message", "订单不存在");
            return result;
        }

        Map<String, Object> orderDetail = new HashMap<>();
        orderDetail.put("orderId", order.getOrderId());
        orderDetail.put("seats", order.getSeats());
        orderDetail.put("totalPrice", order.getTotalPrice());

        String status = order.getStatus();
        if ("pending".equals(status)) {
            status = "待支付";
        } else if ("completed".equals(status)) {
            status = "已完成";
        } else if ("cancelled".equals(status)) {
            status = "已取消";
        }
        orderDetail.put("status", status);

        if (order.getCreateTime() != null) {
            orderDetail.put("createTime", order.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }

        Schedule schedule = scheduleRepository.findById(order.getScheduleId()).orElse(null);
        if (schedule != null) {
            orderDetail.put("hallName", schedule.getHallName());
            orderDetail.put("cinemaName", schedule.getCinemaName());
            orderDetail.put("showTime", schedule.getStartTime());

            Movie movie = movieRepository.findById(schedule.getMovieId()).orElse(null);
            if (movie != null) {
                orderDetail.put("movieTitle", movie.getTitle());
            }
        }

        SysUser user = sysUserRepository.findById(order.getUserId()).orElse(null);
        if (user != null) {
            orderDetail.put("userName", user.getUsername());
            String phone = user.getPhone();
            if (phone != null && phone.length() >= 11) {
                phone = phone.substring(0, 3) + "****" + phone.substring(7);
            }
            orderDetail.put("userPhone", phone);
        }

        result.put("code", 200);
        result.put("message", "success");
        result.put("data", orderDetail);

        return result;
    }
}