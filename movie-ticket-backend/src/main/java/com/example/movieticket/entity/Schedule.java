package com.example.movieticket.entity;

import javax.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "movie_schedule")
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;
    
    @Column(name = "movie_id")
    private Long movieId;
    
    @Column(name = "hall_id")
    private Long hallId;
    
    @Column(name = "hall_name")
    private String hallName;
    
    @Column(name = "show_time")
    private String startTime;
    
    @Column(name = "end_time")
    private String endTime;
    
    private Double price;
    
    private Integer status;
    
    @Column(name = "create_time", updatable = false, insertable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Date createTime;
    
    @Column(name = "update_time", updatable = false, insertable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Date updateTime;
    
    @Column(name = "is_deleted")
    private Integer isDeleted;
}