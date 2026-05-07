package com.example.movieticket.controller;

import com.example.movieticket.entity.Banner;
import com.example.movieticket.service.BannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/banners")
public class BannerController {
    @Autowired
    private BannerService bannerService;

    // 获取所有轮播图
    @GetMapping
    public List<Banner> getAllBanners() {
        List<Banner> allBanners = bannerService.getAllBanners();
        List<Banner> validBanners = new ArrayList<>();
        for (Banner b : allBanners) {
            if (b.getOrderNum() != null && b.getOrderNum() > 0) {
                // 确保status字段有值
                if (b.getStatus() == null) {
                    b.setStatus(1);
                }
                validBanners.add(b);
            }
        }
        return validBanners;
    }

    // 根据ID获取轮播图
    @GetMapping("/{id}")
    public Banner getBannerById(@PathVariable Long id) {
        Banner banner = bannerService.getBannerById(id);
        if (banner != null && banner.getStatus() == null) {
            banner.setStatus(1);
        }
        return banner;
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

    // 批量删除轮播图
    @DeleteMapping("/batch")
    public Map<String, Object> deleteBanners(@RequestBody List<Long> ids) {
        Map<String, Object> response = new HashMap<>();
        try {
            for (Long id : ids) {
                bannerService.deleteBanner(id);
            }
            response.put("code", 200);
            response.put("message", "删除成功");
            response.put("data", true);
        } catch (Exception e) {
            response.put("code", 500);
            response.put("message", "删除失败: " + e.getMessage());
            response.put("data", false);
        }
        return response;
    }
}