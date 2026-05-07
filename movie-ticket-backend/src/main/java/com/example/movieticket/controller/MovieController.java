package com.example.movieticket.controller;

import com.example.movieticket.entity.Movie;
import com.example.movieticket.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class MovieController {
    @Autowired
    private MovieService movieService;

    @GetMapping("/movies")
    public Map<String, Object> getMovies(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String year,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "movieId") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return movieService.getMoviesWithFilter(type, region, year, status, keyword, sort, page, size);
    }

    @GetMapping("/movies/types")
    public Map<String, Object> getMovieTypes() {
        return movieService.getMovieTypes();
    }

    @GetMapping("/movies/regions")
    public Map<String, Object> getRegions() {
        return movieService.getRegions();
    }

    @GetMapping("/movies/years")
    public Map<String, Object> getYears() {
        return movieService.getYears();
    }

    @GetMapping("/movies/{id}")
    public Map<String, Object> getMovieById(@PathVariable Long id) {
        return movieService.getMovieById(id);
    }

    @PostMapping("/movies")
    public Map<String, Object> addMovie(@RequestBody Movie movie) {
        return movieService.addMovie(movie);
    }

    @PostMapping("/add/movies")
    public Map<String, Object> addMovieOld(@RequestBody Movie movie) {
        return movieService.addMovie(movie);
    }

    @PutMapping("/movies/{id}")
    public Map<String, Object> updateMovie(@PathVariable Long id, @RequestBody Movie movie) {
        movie.setMovieId(id);
        return movieService.updateMovie(movie);
    }

    @DeleteMapping("/movies/{id}")
    public Map<String, Object> deleteMovie(@PathVariable Long id) {
        return movieService.deleteMovie(id);
    }

    @GetMapping("/movies/hot")
    public Map<String, Object> getHotMovies() {
        return movieService.getHotMovies();
    }

    @GetMapping("/movies/coming")
    public Map<String, Object> getComingMovies() {
        return movieService.getComingMovies();
    }
}