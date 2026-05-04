package com.example.movieticket.repository;

import com.example.movieticket.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderId(String orderId);

    List<Order> findByUserId(Long userId);

    List<Order> findByUserIdOrderByCreateTimeDesc(Long userId);

    long countByCreateTimeBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.createTime BETWEEN :start AND :end")
    Double sumTotalPriceByCreateTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    List<Order> findTop10ByOrderByCreateTimeDesc();

}
