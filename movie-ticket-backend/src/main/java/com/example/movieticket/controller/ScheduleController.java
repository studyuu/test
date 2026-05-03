package com.example.movieticket.controller;

import com.example.movieticket.entity.Cinema;
import com.example.movieticket.entity.Movie;
import com.example.movieticket.entity.Schedule;
import com.example.movieticket.entity.Seat;
import com.example.movieticket.repository.CinemaRepository;
import com.example.movieticket.repository.MovieRepository;
import com.example.movieticket.repository.ScheduleRepository;
import com.example.movieticket.repository.SeatRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ScheduleController {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private CinemaRepository cinemaRepository;

    @Autowired
    private SeatRepository seatRepository;

    @GetMapping("/schedules")
    public Map<String, Object> getSchedules(
            @RequestParam(required = false) Long movieId,
            @RequestParam(required = false) Long cinemaId) {
        Map<String, Object> response = new HashMap<>();

        List<Schedule> schedules = scheduleRepository.findAll();

        if (movieId != null) {
            schedules = schedules.stream()
                    .filter(s -> movieId.equals(s.getMovieId()))
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

    @GetMapping("/schedules/{id}/detail")
    public Map<String, Object> getScheduleDetail(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        Optional<Schedule> scheduleOpt = scheduleRepository.findById(id);
        if (!scheduleOpt.isPresent()) {
            response.put("code", 404);
            response.put("message", "排期不存在");
            response.put("data", null);
            return response;
        }

        Schedule schedule = scheduleOpt.get();
        Optional<Movie> movieOpt = movieRepository.findById(schedule.getMovieId());
        Optional<Cinema> cinemaOpt = Optional.empty();
        if (schedule.getCinemaId() != null) {
            cinemaOpt = cinemaRepository.findById(schedule.getCinemaId());
        }

        if (!movieOpt.isPresent()) {
            response.put("code", 404);
            response.put("message", "影片不存在");
            response.put("data", null);
            return response;
        }

        Movie movie = movieOpt.get();
        Cinema cinema = cinemaOpt.isPresent() ? cinemaOpt.get() : null;

        List<Seat> seats = seatRepository.findByScheduleId(schedule.getId());

        Map<String, Object> scheduleData = new HashMap<>();
        scheduleData.put("id", schedule.getId());
        scheduleData.put("movieId", schedule.getMovieId());
        scheduleData.put("cinemaId", schedule.getCinemaId());
        scheduleData.put("cinemaName", schedule.getCinemaName());
        scheduleData.put("hallName", schedule.getHallName());
        scheduleData.put("startTime", schedule.getStartTime());
        scheduleData.put("endTime", schedule.getEndTime());
        scheduleData.put("price", schedule.getPrice());
        scheduleData.put("status", schedule.getStatus());

        Map<String, Object> movieData = new HashMap<>();
        movieData.put("id", movie.getMovieId());
        movieData.put("title", movie.getTitle());
        movieData.put("poster", movie.getPoster());

        List<Map<String, Object>> seatsData = new ArrayList<>();
        for (Seat seat : seats) {
            Map<String, Object> seatMap = new HashMap<>();
            seatMap.put("id", seat.getId());
            seatMap.put("row", seat.getRowNum());
            seatMap.put("rowLabel", seat.getRowLabel());
            seatMap.put("col", seat.getColNum());
            seatMap.put("colLabel", seat.getColLabel());
            seatMap.put("status", seat.getStatus());
            seatsData.add(seatMap);
        }

        if (seats.isEmpty()) {
            String[] rowLabels = { "A", "B", "C", "D", "E", "F", "G", "H" };
            for (int rowNum = 1; rowNum <= 8; rowNum++) {
                for (int colNum = 1; colNum <= 12; colNum++) {
                    Seat seat = new Seat();
                    seat.setScheduleId(schedule.getId());
                    seat.setRowNum(rowNum);
                    seat.setRowLabel(rowLabels[rowNum - 1]);
                    seat.setColNum(colNum);
                    seat.setColLabel(String.valueOf(colNum));
                    seat.setStatus("available");
                    seatRepository.save(seat);

                    Map<String, Object> seatMap = new HashMap<>();
                    seatMap.put("id", seat.getId());
                    seatMap.put("row", seat.getRowNum());
                    seatMap.put("rowLabel", seat.getRowLabel());
                    seatMap.put("col", seat.getColNum());
                    seatMap.put("colLabel", seat.getColLabel());
                    seatMap.put("status", seat.getStatus());
                    seatsData.add(seatMap);
                }
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("schedule", scheduleData);
        result.put("movie", movieData);
        result.put("seats", seatsData);

        response.put("code", 200);
        response.put("message", "success");
        response.put("data", result);
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

        if (schedule.getCinemaId() == null) {
            response.put("code", 400);
            response.put("message", "影院ID不能为空");
            response.put("data", null);
            return response;
        }

        if (schedule.getStatus() == null) {
            schedule.setStatus(1);
        }

        if (schedule.getHallId() == null) {
            schedule.setHallId(1L);
        }

        Optional<Cinema> cinema = cinemaRepository.findById(schedule.getCinemaId());
        cinema.ifPresent(c -> schedule.setCinemaName(c.getCinemaName()));

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

            if (schedule.getCinemaId() != null) {
                existing.setCinemaId(schedule.getCinemaId());
                Optional<Cinema> cinema = cinemaRepository.findById(schedule.getCinemaId());
                cinema.ifPresent(c -> existing.setCinemaName(c.getCinemaName()));
            }
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
        map.put("cinemaId", schedule.getCinemaId());
        map.put("cinemaName", schedule.getCinemaName());
        map.put("hallId", schedule.getHallId());
        map.put("hallName", schedule.getHallName());
        map.put("startTime", schedule.getStartTime());
        map.put("endTime", schedule.getEndTime());
        map.put("price", schedule.getPrice());
        map.put("status", schedule.getStatus());
        map.put("createTime", schedule.getCreateTime());
        map.put("updateTime", schedule.getUpdateTime());

        if (schedule.getMovieId() != null) {
            Optional<Movie> movie = movieRepository.findById(schedule.getMovieId());
            movie.ifPresent(m -> map.put("movieName", m.getTitle()));
        }

        return map;
    }
}