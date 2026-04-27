package com.example.movieticket.entity;

import javax.persistence.*;
import lombok.Data;
import java.util.Date;

@Data
@Entity
@Table(name = "cinema")
public class Cinema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cinema_id")
    private Long cinemaId;
    
    @Column(name = "cinema_name")
    private String cinemaName;
    
    private String address;
    private String phone;
    private String description;
    private String logo;
    private Integer status;
    private Double rating;
    
    @Column(name = "create_time", updatable = false, insertable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Date createTime;
    
    @Column(name = "update_time", updatable = false, insertable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private Date updateTime;
    
    @Column(name = "is_deleted")
    private Integer isDeleted;
    
    @Column(name = "is_hot")
    private Boolean isHot;
}