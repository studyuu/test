package com.example.movieticket.service;

import com.example.movieticket.entity.Movie;
import com.example.movieticket.entity.WishList;
import com.example.movieticket.repository.MovieRepository;
import com.example.movieticket.repository.WishListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WishListService {

    @Autowired
    private WishListRepository wishListRepository;

    @Autowired
    private MovieRepository movieRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Map<String, Object> addWish(Long userId, Long movieId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (wishListRepository.existsByUserIdAndMovieId(userId, movieId)) {
                response.put("code", 400);
                response.put("message", "已添加到想看列表");
                response.put("data", false);
                return response;
            }

            WishList wishList = new WishList(userId, movieId);
            wishListRepository.save(wishList);

            response.put("code", 200);
            response.put("message", "添加想看成功");
            response.put("data", true);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "添加失败: " + e.getMessage());
            response.put("data", false);
        }
        return response;
    }

    @Transactional
    public Map<String, Object> removeWish(Long userId, Long movieId) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!wishListRepository.existsByUserIdAndMovieId(userId, movieId)) {
                response.put("code", 400);
                response.put("message", "未添加到想看列表");
                response.put("data", false);
                return response;
            }

            wishListRepository.deleteByUserIdAndMovieId(userId, movieId);

            response.put("code", 200);
            response.put("message", "取消想看成功");
            response.put("data", true);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "取消失败: " + e.getMessage());
            response.put("data", false);
        }
        return response;
    }

    public Map<String, Object> checkWish(Long userId, Long movieId) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean exists = wishListRepository.existsByUserIdAndMovieId(userId, movieId);
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", exists);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
            response.put("data", false);
        }
        return response;
    }

    public Map<String, Object> getWishList(Long userId, int page, int size) {
        Map<String, Object> response = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page - 1, size);
            Page<WishList> wishPage = wishListRepository.findByUserIdOrderByAddedTimeDesc(userId, pageable);

            List<Map<String, Object>> movies = wishPage.getContent().stream()
                    .map(wish -> {
                        Map<String, Object> movieMap = new HashMap<>();
                        Optional<Movie> movieOpt = movieRepository.findById(wish.getMovieId());
                        if (movieOpt.isPresent()) {
                            Movie movie = movieOpt.get();
                            movieMap.put("movieId", movie.getMovieId());
                            movieMap.put("title", movie.getTitle());
                            movieMap.put("poster", movie.getPoster());
                            movieMap.put("category", movie.getCategory());
                            movieMap.put("rating", movie.getRating());
                            movieMap.put("releaseDate", movie.getReleaseDate());
                        }
                        movieMap.put("addedTime",
                                wish.getAddedTime() != null ? wish.getAddedTime().format(FORMATTER) : null);
                        return movieMap;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> data = new HashMap<>();
            data.put("content", movies);
            data.put("totalElements", wishPage.getTotalElements());
            data.put("totalPages", wishPage.getTotalPages());
            data.put("size", wishPage.getSize());
            data.put("number", wishPage.getNumber());

            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", data);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
        }
        return response;
    }
}