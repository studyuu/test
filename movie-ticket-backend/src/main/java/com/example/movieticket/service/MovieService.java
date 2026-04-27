package com.example.movieticket.service;

import com.example.movieticket.entity.Movie;
import com.example.movieticket.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MovieService {
    @Autowired
    private MovieRepository movieRepository;
    
    public Map<String, Object> getAllMovies() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", movieRepository.findAll());
        return response;
    }
    
    public Map<String, Object> getMoviesWithFilter(String type, String region, String year, Integer status, String keyword, String sortBy, int page, int size) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (keyword != null) {
                keyword = keyword.trim();
                if (keyword.isEmpty()) {
                    keyword = null;
                }
            }
            
            String typePattern = type != null && !"全部".equals(type) ? "%" + type + "%" : null;
            String regionPattern = region != null && !"全部".equals(region) ? "%" + region + "%" : null;
            String yearPattern = year != null && !"全部".equals(year) && !"更早".equals(year) ? year + "%" : null;
            String keywordPattern = keyword != null ? "%" + keyword + "%" : null;
            
            Sort sort = Sort.by(Sort.Direction.DESC, "rating");
            
            if ("new".equals(sortBy)) {
                sort = Sort.by(Sort.Direction.DESC, "releaseDate");
            } else if ("rating".equals(sortBy)) {
                sort = Sort.by(Sort.Direction.DESC, "rating");
            } else if ("price".equals(sortBy)) {
                sort = Sort.by(Sort.Direction.ASC, "price");
            } else {
                sort = Sort.by(Sort.Direction.DESC, "rating");
            }
            
            Pageable pageable = PageRequest.of(page - 1, size, sort);
            
            Page<Movie> moviePage = movieRepository.findWithFilters(type, typePattern, region, regionPattern, year, yearPattern, status, keyword, keywordPattern, pageable);
            
            Map<String, Object> data = new HashMap<>();
            data.put("content", moviePage.getContent());
            data.put("totalElements", moviePage.getTotalElements());
            data.put("totalPages", moviePage.getTotalPages());
            data.put("size", moviePage.getSize());
            data.put("number", moviePage.getNumber());
            
            response.put("code", 200);
            response.put("message", "查询成功");
            response.put("data", data);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "查询失败: " + e.getMessage());
        }
        return response;
    }
    
    public Map<String, Object> getMovieTypes() {
        Map<String, Object> response = new HashMap<>();
        List<String> types = Arrays.asList("爱情", "喜剧", "动作", "科幻", "动画", "剧情", "悬疑", "恐怖");
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", types);
        return response;
    }
    
    public Map<String, Object> getRegions() {
        Map<String, Object> response = new HashMap<>();
        List<String> regions = Arrays.asList("中国大陆", "美国", "韩国", "日本", "中国香港", "中国台湾", "其他");
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", regions);
        return response;
    }
    
    public Map<String, Object> getYears() {
        Map<String, Object> response = new HashMap<>();
        List<String> years = Arrays.asList("2025", "2024", "2023", "2022", "2021", "2020", "更早");
        response.put("code", 200);
        response.put("message", "查询成功");
        response.put("data", years);
        return response;
    }
    
    public Map<String, Object> getMovieById(Long id) {
        Map<String, Object> response = new HashMap<>();
        Movie movie = movieRepository.findById(id).orElse(null);
        if (movie != null) {
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", movie);
        } else {
            response.put("code", 404);
            response.put("message", "电影不存在");
        }
        return response;
    }
    
    public Map<String, Object> addMovie(Movie movie) {
        Map<String, Object> response = new HashMap<>();
        try {
            System.out.println("添加影片: " + movie);
            Movie savedMovie = movieRepository.save(movie);
            response.put("code", 200);
            response.put("message", "添加成功");
            response.put("data", savedMovie);
        } catch (Exception e) {
            System.out.println("添加影片失败: " + e.getMessage());
            e.printStackTrace();
            response.put("code", 500);
            response.put("message", "添加失败: " + e.getMessage());
        }
        return response;
    }
    
    public Map<String, Object> updateMovie(Movie movie) {
        Map<String, Object> response = new HashMap<>();
        try {
            Movie updatedMovie = movieRepository.save(movie);
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", updatedMovie);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新失败: " + e.getMessage());
        }
        return response;
    }
    
    public Map<String, Object> deleteMovie(Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            movieRepository.deleteById(id);
            response.put("code", 200);
            response.put("message", "删除成功");
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败: " + e.getMessage());
        }
        return response;
    }
    
    public Map<String, Object> getHotMovies() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", movieRepository.findByStatusOrderByRatingDesc(1));
        return response;
    }
    
    public Map<String, Object> getComingMovies() {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", movieRepository.findByStatus(2));
        return response;
    }
}