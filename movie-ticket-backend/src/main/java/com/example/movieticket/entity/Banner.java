package com.example.movieticket.entity;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "banner")
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String image;
    private String title;
    private String description;
    private Long movieId;
    
    @Column(name = "order_num")
    private Integer orderNum;
    
    private Integer status;
}