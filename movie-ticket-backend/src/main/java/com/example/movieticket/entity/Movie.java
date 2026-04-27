package com.example.movieticket.entity;

import javax.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "movie")
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long movieId;
    
    @Column(name = "movie_name")
    private String title;
    
    private String poster;
    
    @Column(name = "movie_type")
    private String category;
    
    @Column(name = "region")
    private String region;
    
    private Integer duration;
    private String director;
    private String actors;
    private String synopsis;
    
    @Column(name = "release_date")
    private String releaseDate;
    
    private Integer status;
    private Double rating;
    
    @Column(name = "english_title")
    private String englishTitle;
    
    private Double price;
}