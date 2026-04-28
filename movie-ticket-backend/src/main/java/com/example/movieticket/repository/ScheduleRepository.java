package com.example.movieticket.repository;

import com.example.movieticket.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByMovieId(Long movieId);
    
    List<Schedule> findByHallId(Long hallId);
    
    List<Schedule> findByStatus(Integer status);
}