package com.example.movieticket.service;

import com.example.movieticket.entity.Cinema;
import com.example.movieticket.repository.CinemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CinemaService {
    @Autowired
    private CinemaRepository cinemaRepository;
    
    public List<Cinema> getHotCinemas() {
        return cinemaRepository.findByIsHotTrueOrderByRatingDesc();
    }
}