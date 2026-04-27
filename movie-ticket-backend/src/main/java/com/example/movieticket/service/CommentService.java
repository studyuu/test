package com.example.movieticket.service;

import com.example.movieticket.entity.Comment;
import com.example.movieticket.entity.CommentLike;
import com.example.movieticket.entity.SysUser;
import com.example.movieticket.repository.CommentLikeRepository;
import com.example.movieticket.repository.CommentRepository;
import com.example.movieticket.repository.SysUserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private SysUserRepository userRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final Long DEFAULT_USER_ID = 1L;

    public Comment saveComment(Long movieId, Long userId, Integer rating, String content, List<String> images) {
        String imagesJson = null;
        if (images != null && !images.isEmpty()) {
            try {
                imagesJson = objectMapper.writeValueAsString(images);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }

        Comment comment = new Comment();
        comment.setMovieId(movieId);
        comment.setUserId(userId);

        // 从数据库获取用户信息
        Optional<SysUser> user = userRepository.findById(userId);
        if (user.isPresent()) {
            comment.setUserName(user.get().getNickname());
            comment.setAvatar(user.get().getAvatar());
        } else {
            comment.setUserName("当前用户");
            comment.setAvatar("https://picsum.photos/100/100?random=99");
        }

        comment.setRating(rating);
        comment.setContent(content);
        comment.setImages(imagesJson);
        comment.setLikes(0);

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByMovieId(Long movieId) {
        return commentRepository.findByMovieIdOrderByCreateTimeDesc(movieId);
    }

    public Comment likeComment(Long id, boolean liked) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment != null) {
            Optional<CommentLike> existingLike = commentLikeRepository.findByCommentIdAndUserId(id, DEFAULT_USER_ID);

            if (liked) {
                // 取消点赞
                if (comment.getLikes() > 0) {
                    comment.setLikes(comment.getLikes() - 1);
                }
                // 删除点赞记录
                existingLike.ifPresent(like -> commentLikeRepository.delete(like));
            } else {
                // 点赞
                comment.setLikes(comment.getLikes() + 1);
                // 创建或更新点赞记录
                CommentLike like = existingLike.orElse(new CommentLike());
                like.setCommentId(id);
                like.setUserId(DEFAULT_USER_ID);
                like.setLiked(true);
                commentLikeRepository.save(like);
            }
            return commentRepository.save(comment);
        }
        return null;
    }

    public boolean isCommentLiked(Long commentId) {
        return commentLikeRepository.findByCommentIdAndUserId(commentId, DEFAULT_USER_ID)
                .map(CommentLike::getLiked)
                .orElse(false);
    }

    public Map<Long, Boolean> getLikedStatusForComments(List<Long> commentIds) {
        List<CommentLike> likes = commentLikeRepository.findByCommentIdIn(commentIds);
        return likes.stream()
                .filter(like -> Boolean.TRUE.equals(like.getLiked()))
                .collect(Collectors.toMap(CommentLike::getCommentId, CommentLike::getLiked));
    }

    public Map<String, Object> toResponseMap(Comment comment) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", comment.getId());
        map.put("movieId", comment.getMovieId());
        map.put("userId", comment.getUserId());
        map.put("userName", comment.getUserName());
        map.put("avatar", comment.getAvatar());
        map.put("rating", comment.getRating());
        map.put("content", comment.getContent());

        if (comment.getImages() != null && !comment.getImages().isEmpty()) {
            try {
                List<String> images = objectMapper.readValue(comment.getImages(), List.class);
                map.put("images", images);
            } catch (JsonProcessingException e) {
                map.put("images", null);
            }
        } else {
            map.put("images", null);
        }

        map.put("likes", comment.getLikes());

        String createTime = comment.getCreateTime().toString().replace("T", " ");
        if (createTime.length() > 16) {
            createTime = createTime.substring(0, 16);
        }
        map.put("createTime", createTime);

        return map;
    }

    public Comment getCommentById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    public List<Comment> getAllComments() {
        return commentRepository.findAllByOrderByCreateTimeDesc();
    }

    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}