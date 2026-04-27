package com.example.movieticket.controller;

import com.example.movieticket.entity.Comment;
import com.example.movieticket.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @GetMapping("/movie/{movieId}")
    public Map<String, Object> getCommentsByMovie(@PathVariable Long movieId) {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Comment> comments = commentService.getCommentsByMovieId(movieId);

            // 获取所有评论ID
            List<Long> commentIds = comments.stream()
                    .map(Comment::getId)
                    .collect(Collectors.toList());

            // 获取用户点赞状态
            Map<Long, Boolean> likedStatus = commentService.getLikedStatusForComments(commentIds);

            // 构建评论列表，添加点赞状态
            List<Map<String, Object>> commentList = comments.stream()
                    .map(comment -> {
                        Map<String, Object> map = commentService.toResponseMap(comment);
                        map.put("liked", likedStatus.getOrDefault(comment.getId(), false));
                        return map;
                    })
                    .collect(Collectors.toList());

            response.put("code", 200);
            response.put("message", "success");
            response.put("data", commentList);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取评论失败: " + e.getMessage());
            response.put("data", null);
        }

        return response;
    }

    @PostMapping
    public Map<String, Object> createComment(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            Long movieId = Long.parseLong(request.get("movieId").toString());
            Long userId = request.get("userId") != null ? Long.parseLong(request.get("userId").toString()) : 1L;
            Integer rating = (Integer) request.get("rating");
            String content = (String) request.get("content");
            List<String> images = (List<String>) request.get("images");

            Comment comment = commentService.saveComment(movieId, userId, rating, content, images);

            response.put("code", 200);
            response.put("message", "发表评论成功");
            response.put("data", commentService.toResponseMap(comment));
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "发表评论失败: " + e.getMessage());
            response.put("data", null);
        }

        return response;
    }

    @PostMapping("/{id}/like")
    public Map<String, Object> likeComment(@PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean liked = request != null && Boolean.TRUE.equals(request.get("liked"));
            Comment comment = commentService.likeComment(id, liked);

            String message = liked ? "取消点赞成功" : "点赞成功";
            response.put("code", 200);
            response.put("message", message);
            response.put("data", commentService.toResponseMap(comment));
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "操作失败: " + e.getMessage());
            response.put("data", null);
        }

        return response;
    }

    @GetMapping("/{id}")
    public Map<String, Object> getCommentById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            Comment comment = commentService.getCommentById(id);
            if (comment != null) {
                response.put("code", 200);
                response.put("message", "success");
                response.put("data", commentService.toResponseMap(comment));
            } else {
                response.put("code", 404);
                response.put("message", "评论不存在");
                response.put("data", null);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取评论详情失败: " + e.getMessage());
            response.put("data", null);
        }

        return response;
    }

    @GetMapping
    public Map<String, Object> getAllComments() {
        Map<String, Object> response = new HashMap<>();

        try {
            List<Comment> comments = commentService.getAllComments();
            List<Map<String, Object>> commentList = comments.stream()
                    .map(commentService::toResponseMap)
                    .collect(Collectors.toList());

            response.put("code", 200);
            response.put("message", "success");
            response.put("data", commentList);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取评论列表失败: " + e.getMessage());
            response.put("data", null);
        }

        return response;
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> deleteComment(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        try {
            commentService.deleteComment(id);
            response.put("code", 200);
            response.put("message", "删除成功");
            response.put("data", null);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败: " + e.getMessage());
            response.put("data", null);
        }

        return response;
    }
}
