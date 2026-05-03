package com.example.movieticket.repository;

import com.example.movieticket.entity.WishList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishListRepository extends JpaRepository<WishList, Long> {

    Optional<WishList> findByUserIdAndMovieId(Long userId, Long movieId);

    boolean existsByUserIdAndMovieId(Long userId, Long movieId);

    void deleteByUserIdAndMovieId(Long userId, Long movieId);

    Page<WishList> findByUserIdOrderByAddedTimeDesc(Long userId, Pageable pageable);

    @Query("SELECT w FROM WishList w WHERE w.userId = :userId ORDER BY w.addedTime DESC")
    List<WishList> findByUserIdOrdered(Long userId);
}