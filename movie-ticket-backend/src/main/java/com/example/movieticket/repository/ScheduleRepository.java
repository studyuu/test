package com.example.movieticket.repository;

import com.example.movieticket.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByMovieId(Long movieId);
    
    List<Schedule> findByHallId(Long hallId);
    
    List<Schedule> findByStatus(Integer status);

    List<Schedule> findByCinemaId(Long cinemaId);

    @Query("SELECT s FROM Schedule s WHERE s.cinemaId = :cinemaId AND s.startTime >= :startOfDay AND s.startTime < :endOfDay ORDER BY s.startTime")
    List<Schedule> findByCinemaIdAndDate(@Param("cinemaId") Long cinemaId, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    long countByHallId(Long hallId);
}