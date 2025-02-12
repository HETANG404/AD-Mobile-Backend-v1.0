package com.tang.demo_db.controller;


import com.tang.demo_db.entity.Restaurant;
import com.tang.demo_db.service.RestaurantService;
import com.tang.demo_db.service.RestaurantServiceZP;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantControllerZP {


    private final RestaurantServiceZP restaurantService;
    @GetMapping("/recommend")
    public ResponseEntity<String> getRecommendedRestaurants(@RequestParam String query) {
        return restaurantService.fetchRecommendedRestaurants(query);
    }

    // 添加餐厅到收藏
    @PostMapping("/favorite/{userId}")
    public ResponseEntity<String> addFavorite(@PathVariable String userId, @RequestBody  Restaurant restaurant) {
        restaurantService.addRestaurantToFavorites(userId, restaurant);
        return ResponseEntity.ok("收藏成功");
    }

    // 取消收藏
    @DeleteMapping("/favorite/{userId}/{restaurantId}")
    public ResponseEntity<String> removeFavorite(@PathVariable String userId, @PathVariable String restaurantId) {
        restaurantService.removeRestaurantFromFavorites(userId, restaurantId);
        return ResponseEntity.ok("取消收藏成功");
    }
    // 获取用户收藏的餐厅
    @GetMapping("/favorites/{userId}")
    public ResponseEntity<List<Restaurant>> getFavoriteRestaurants(@PathVariable String userId) {
        List<Restaurant> favorites = restaurantService.getUserFavoriteRestaurants(userId);
        return ResponseEntity.ok(favorites);
    }


}

/*
    @GetMapping("/top-rated")
    public ResponseEntity<List<Restaurant>> getTopRatedRestaurants(@RequestParam float minRating) {
        List<Restaurant> restaurants = restaurantService.getTopRatedRestaurants(minRating);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();
        return ResponseEntity.ok(restaurants);
    }

*/


