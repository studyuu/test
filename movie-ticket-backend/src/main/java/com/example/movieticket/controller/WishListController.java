package com.example.movieticket.controller;

import com.example.movieticket.service.WishListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class WishListController {

    @Autowired
    private WishListService wishListService;

    @PostMapping("/movies/{movieId}/wish")
    public Map<String, Object> addWish(@PathVariable Long movieId, @RequestParam Long userId) {
        return wishListService.addWish(userId, movieId);
    }

    @DeleteMapping("/movies/{movieId}/wish")
    public Map<String, Object> removeWish(@PathVariable Long movieId, @RequestParam Long userId) {
        return wishListService.removeWish(userId, movieId);
    }

    @GetMapping("/movies/{movieId}/wish")
    public Map<String, Object> checkWish(@PathVariable Long movieId, @RequestParam Long userId) {
        return wishListService.checkWish(userId, movieId);
    }

    @GetMapping("/movies/wishlist")
    public Map<String, Object> getWishList(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return wishListService.getWishList(userId, page, size);
    }
}