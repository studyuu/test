package com.example.movieticket.repository;

import com.example.movieticket.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByMovieIdOrderByCreateTimeDesc(Long movieId);
    List<Comment> findAllByOrderByCreateTimeDesc();
}
