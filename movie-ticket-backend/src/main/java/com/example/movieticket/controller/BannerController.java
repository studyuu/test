package com.example.movieticket.controller;

import com.example.movieticket.entity.Banner;
import com.example.movieticket.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
public class BannerController {
    @Autowired
    private BannerService bannerService;
    
    // 获取所有轮播图
    @GetMapping
    public List<Banner> getAllBanners() {
        return bannerService.getAllBanners();
    }
    
    // 根据ID获取轮播图
    @GetMapping("/{id}")
    public Banner getBannerById(@PathVariable Long id) {
        return bannerService.getBannerById(id);
    }
    
    // 添加轮播图
    @PostMapping
    public Banner addBanner(@RequestBody Banner banner) {
        return bannerService.saveBanner(banner);
    }
    
    // 更新轮播图
    @PutMapping("/{id}")
    public Banner updateBanner(@PathVariable Long id, @RequestBody Banner banner) {
        banner.setId(id);
        return bannerService.saveBanner(banner);
    }
    
    // 删除轮播图
    @DeleteMapping("/{id}")
    public void deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
    }
}