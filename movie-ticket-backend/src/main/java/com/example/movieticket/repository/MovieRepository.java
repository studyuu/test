package com.example.movieticket.repository;

import com.example.movieticket.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByStatusOrderByRatingDesc(Integer status);
    List<Movie> findByStatus(Integer status);
    
    @Query("SELECT m FROM Movie m WHERE " +
           "(:type IS NULL OR :type = '全部' OR m.category LIKE :typePattern) AND " +
           "(:region IS NULL OR :region = '全部' OR m.region LIKE :regionPattern) AND " +
           "(:year IS NULL OR :year = '全部' OR (m.releaseDate LIKE :yearPattern OR (:year = '更早' AND SUBSTRING(m.releaseDate, 1, 4) < '2020'))) AND " +
           "(:status IS NULL OR m.status = :status) AND " +
           "(:keyword IS NULL OR :keyword = '' OR m.title LIKE :keywordPattern OR m.actors LIKE :keywordPattern OR m.director LIKE :keywordPattern)")
    Page<Movie> findWithFilters(@Param("type") String type,
                               @Param("typePattern") String typePattern,
                               @Param("region") String region,
                               @Param("regionPattern") String regionPattern,
                               @Param("year") String year,
                               @Param("yearPattern") String yearPattern,
                               @Param("status") Integer status,
                               @Param("keyword") String keyword,
                               @Param("keywordPattern") String keywordPattern,
                               Pageable pageable);
}