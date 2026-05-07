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

    @Column(name = "movie_name", length = 255)
    private String title;

    @Column(length = 500)
    private String poster;

    @Column(name = "movie_type", length = 50)
    private String category;

    @Column(name = "region", length = 50)
    private String region;

    private Integer duration;

    @Column(length = 100)
    private String director;

    @Column(length = 500)
    private String actors;

    @Column(length = 2000)
    private String synopsis;

    @Column(name = "release_date", length = 20)
    private String releaseDate;

    private Integer status;
    private Double rating;

    @Column(name = "english_title", length = 255)
    private String englishTitle;

    private Double price;
}