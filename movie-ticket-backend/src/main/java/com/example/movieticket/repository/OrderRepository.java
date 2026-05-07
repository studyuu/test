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

    List<Order> findAllByOrderByCreateTimeDesc();

    @Query("SELECT o FROM Order o JOIN Schedule s ON o.scheduleId = s.id WHERE s.cinemaId = :cinemaId ORDER BY o.createTime DESC")
    List<Order> findByCinemaId(@Param("cinemaId") Long cinemaId);

    @Query("SELECT o FROM Order o JOIN Schedule s ON o.scheduleId = s.id WHERE s.cinemaId = :cinemaId AND o.status = :status")
    List<Order> findByCinemaIdAndStatus(@Param("cinemaId") Long cinemaId, @Param("status") String status);

    @Query("SELECT SUM(o.totalPrice) FROM Order o JOIN Schedule s ON o.scheduleId = s.id WHERE s.cinemaId = :cinemaId AND o.createTime BETWEEN :start AND :end")
    Double sumTotalPriceByCinemaIdAndCreateTimeBetween(@Param("cinemaId") Long cinemaId,
            @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT o FROM Order o JOIN Schedule s ON o.scheduleId = s.id WHERE s.cinemaId = :cinemaId ORDER BY o.createTime DESC")
    List<Order> findRecentOrdersByCinemaId(@Param("cinemaId") Long cinemaId);

    @Query("SELECT s.movieId, SUM(o.totalPrice) as sales FROM Order o JOIN Schedule s ON o.scheduleId = s.id WHERE s.cinemaId = :cinemaId GROUP BY s.movieId ORDER BY sales DESC")
    List<Object[]> getTopMoviesByCinemaId(@Param("cinemaId") Long cinemaId);

    long countByScheduleId(Long scheduleId);

    List<Order> findByScheduleId(Long scheduleId);

}