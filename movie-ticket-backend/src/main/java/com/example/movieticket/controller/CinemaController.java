package com.example.movieticket.controller;

import com.example.movieticket.entity.Cinema;
import com.example.movieticket.repository.CinemaRepository;
import com.example.movieticket.service.CinemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/cinemas")
public class CinemaController {
    
    @Autowired
    private CinemaService cinemaService;
    
    @Autowired
    private CinemaRepository cinemaRepository;
    
    @GetMapping("/hot")
    public Map<String, Object> getHotCinemas() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<Cinema> cinemas = cinemaService.getHotCinemas();
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", cinemas);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取热门影院失败");
            response.put("data", null);
        }
        return response;
    }
    
    @GetMapping("/{id}")
    public Map<String, Object> getCinemaById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Cinema> cinema = cinemaRepository.findById(id);
            if (cinema.isPresent()) {
                response.put("code", 200);
                response.put("message", "success");
                response.put("data", cinema.get());
            } else {
                response.put("code", 404);
                response.put("message", "影院不存在");
                response.put("data", null);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取影院详情失败");
            response.put("data", null);
        }
        return response;
    }
    
    @GetMapping
    public Map<String, Object> getAllCinemas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> response = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
            Page<Cinema> cinemaPage = cinemaRepository.findAll(pageable);
            response.put("code", 200);
            response.put("message", "success");
            response.put("data", cinemaPage);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "获取影院列表失败");
            response.put("data", null);
        }
        return response;
    }
    
    @PostMapping
    public Map<String, Object> addCinema(@RequestBody Cinema cinema) {
        Map<String, Object> response = new HashMap<>();
        try {
            cinema.setIsDeleted(0);
            cinema.setIsHot(false);
            Cinema savedCinema = cinemaRepository.save(cinema);
            response.put("code", 200);
            response.put("message", "添加成功");
            response.put("data", savedCinema);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "添加失败: " + e.getMessage());
            response.put("data", null);
        }
        return response;
    }
    
    @PutMapping("/{id}")
    public Map<String, Object> updateCinema(@PathVariable Long id, @RequestBody Cinema cinema) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Cinema> existingCinema = cinemaRepository.findById(id);
            if (existingCinema.isPresent()) {
                Cinema c = existingCinema.get();
                if (cinema.getCinemaName() != null) c.setCinemaName(cinema.getCinemaName());
                if (cinema.getAddress() != null) c.setAddress(cinema.getAddress());
                if (cinema.getPhone() != null) c.setPhone(cinema.getPhone());
                if (cinema.getDescription() != null) c.setDescription(cinema.getDescription());
                if (cinema.getLogo() != null) c.setLogo(cinema.getLogo());
                if (cinema.getStatus() != null) c.setStatus(cinema.getStatus());
                if (cinema.getRating() != null) c.setRating(cinema.getRating());
                if (cinema.getIsHot() != null) c.setIsHot(cinema.getIsHot());
                
                Cinema updatedCinema = cinemaRepository.save(c);
                response.put("code", 200);
                response.put("message", "更新成功");
                response.put("data", updatedCinema);
            } else {
                response.put("code", 404);
                response.put("message", "影院不存在");
                response.put("data", null);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "更新失败: " + e.getMessage());
            response.put("data", null);
        }
        return response;
    }
    
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteCinema(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (cinemaRepository.existsById(id)) {
                cinemaRepository.deleteById(id);
                response.put("code", 200);
                response.put("message", "删除成功");
                response.put("data", null);
            } else {
                response.put("code", 404);
                response.put("message", "影院不存在");
                response.put("data", null);
            }
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败: " + e.getMessage());
            response.put("data", null);
        }
        return response;
    }
}