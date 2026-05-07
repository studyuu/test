package com.example.movieticket.service;

import com.example.movieticket.entity.Banner;
import com.example.movieticket.repository.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BannerService {
    @Autowired
    private BannerRepository bannerRepository;

    public List<Banner> getAllBanners() {
        return bannerRepository.findAllByOrderByOrderNumAsc();
    }

    public Banner getBannerById(Long id) {
        return bannerRepository.findById(id).orElse(null);
    }

    public Banner saveBanner(Banner banner) {
        if (banner.getStatus() == null) {
            banner.setStatus(1);
        }
        return bannerRepository.save(banner);
    }

    public void deleteBanner(Long id) {
        bannerRepository.deleteById(id);
    }

    public List<Banner> getBanners() {
        return bannerRepository.findAllByOrderByOrderNumAsc();
    }
}