package com.example.movieticket.repository;

import com.example.movieticket.entity.Hall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HallRepository extends JpaRepository<Hall, Long> {
    List<Hall> findByCinemaId(Long cinemaId);
    List<Hall> findByCinemaIdAndStatus(Long cinemaId, Integer status);
    List<Hall> findByCinemaIdAndIsDeleted(Long cinemaId, Integer isDeleted);
}