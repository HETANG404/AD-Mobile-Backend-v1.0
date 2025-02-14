package com.tang.demo_db.repository;

import com.tang.demo_db.entity.Favourite;
import com.tang.demo_db.entity.Restaurant;
import com.tang.demo_db.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long> {

    boolean existsByUserAndRestaurant(User user, Restaurant restaurant);
    boolean existsByUserIdAndRestaurantId(Long userId, long restaurantId);
    boolean existsByUserIdAndRestaurant_PlaceId(Long userId, String placeId);
    void deleteByUserAndRestaurant(User user, Restaurant restaurant);
}
