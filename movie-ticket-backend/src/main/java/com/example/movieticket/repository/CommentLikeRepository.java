package com.example.movieticket.repository;

import com.example.movieticket.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentIdAndUserId(Long commentId, Long userId);
    
    List<CommentLike> findByCommentIdIn(List<Long> commentIds);
    
    void deleteByCommentIdAndUserId(Long commentId, Long userId);
}