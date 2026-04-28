package com.example.movieticket.controller;

import com.example.movieticket.entity.Movie;
import com.example.movieticket.entity.Schedule;
import com.example.movieticket.repository.MovieRepository;
import com.example.movieticket.repository.ScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ScheduleController {
    @Autowired
    private ScheduleRepository scheduleRepository;
    
    @Autowired
    private MovieRepository movieRepository;

    @GetMapping("/schedules")
    public Map<String, Object> getSchedules(
            @RequestParam(required = false) Long movieId,
            @RequestParam(required = false) Long cinemaId) {
        Map<String, Object> response = new HashMap<>();
        
        List<Schedule> schedules = scheduleRepository.findAll();
        
        if (movieId != null) {
            schedules = schedules.stream()
                    .filter(s -> s.getMovieId().equals(movieId))
                    .collect(Collectors.toList());
        }
        
        List<Map<String, Object>> scheduleList = schedules.stream()
                .filter(s -> s.getIsDeleted() == null || s.getIsDeleted() == 0)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", scheduleList);
        return response;
    }

    @GetMapping("/schedules/{id}")
    public Map<String, Object> getScheduleById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<Schedule> scheduleOptional = scheduleRepository.findById(id);
        if (scheduleOptional.isPresent()) {
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", convertToResponse(scheduleOptional.get()));
        } else {
            response.put("code", 404);
            response.put("message", "排期不存在");
            response.put("data", null);
        }
        
        return response;
    }

    @PostMapping("/schedules")
    public Map<String, Object> addSchedule(@RequestBody Schedule schedule) {
        Map<String, Object> response = new HashMap<>();
        
        if (schedule.getMovieId() == null) {
            response.put("code", 400);
            response.put("message", "影片ID不能为空");
            response.put("data", null);
            return response;
        }
        
        if (schedule.getStatus() == null) {
            schedule.setStatus(1);
        }
        schedule.setIsDeleted(0);
        
        Schedule savedSchedule = scheduleRepository.save(schedule);
        
        response.put("code", 200);
        response.put("message", "添加成功");
        response.put("data", convertToResponse(savedSchedule));
        return response;
    }

    @PutMapping("/schedules/{id}")
    public Map<String, Object> updateSchedule(@PathVariable Long id, @RequestBody Schedule schedule) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<Schedule> existingOptional = scheduleRepository.findById(id);
        if (existingOptional.isPresent()) {
            Schedule existing = existingOptional.get();
            
            if (schedule.getHallId() != null) {
                existing.setHallId(schedule.getHallId());
            }
            if (schedule.getHallName() != null) {
                existing.setHallName(schedule.getHallName());
            }
            if (schedule.getStartTime() != null) {
                existing.setStartTime(schedule.getStartTime());
            }
            if (schedule.getEndTime() != null) {
                existing.setEndTime(schedule.getEndTime());
            }
            if (schedule.getPrice() != null) {
                existing.setPrice(schedule.getPrice());
            }
            if (schedule.getStatus() != null) {
                existing.setStatus(schedule.getStatus());
            }
            
            Schedule updated = scheduleRepository.save(existing);
            
            response.put("code", 200);
            response.put("message", "更新成功");
            response.put("data", convertToResponse(updated));
        } else {
            response.put("code", 404);
            response.put("message", "排期不存在");
            response.put("data", null);
        }
        
        return response;
    }

    @DeleteMapping("/schedules/{id}")
    public Map<String, Object> deleteSchedule(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<Schedule> existingOptional = scheduleRepository.findById(id);
        if (existingOptional.isPresent()) {
            Schedule schedule = existingOptional.get();
            schedule.setIsDeleted(1);
            scheduleRepository.save(schedule);
            
            response.put("code", 200);
            response.put("message", "删除成功");
            response.put("data", null);
        } else {
            response.put("code", 404);
            response.put("message", "排期不存在");
            response.put("data", null);
        }
        
        return response;
    }

    private Map<String, Object> convertToResponse(Schedule schedule) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", schedule.getId());
        map.put("movieId", schedule.getMovieId());
        map.put("hallId", schedule.getHallId());
        map.put("hallName", schedule.getHallName());
        map.put("startTime", schedule.getStartTime());
        map.put("endTime", schedule.getEndTime());
        map.put("price", schedule.getPrice());
        map.put("status", schedule.getStatus());
        map.put("createTime", schedule.getCreateTime());
        map.put("updateTime", schedule.getUpdateTime());
        
        Optional<Movie> movie = movieRepository.findById(schedule.getMovieId());
        movie.ifPresent(m -> map.put("movieName", m.getTitle()));
        
        map.put("cinemaName", "万达影城（CBD店）");
        
        return map;
    }
}