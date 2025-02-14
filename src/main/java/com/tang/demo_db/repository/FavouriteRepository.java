package com.tang.demo_db.repository;

import com.tang.demo_db.entity.Favourite;
import com.tang.demo_db.entity.Restaurant;
import com.tang.demo_db.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavouriteRepository extends JpaRepository<Favourite, Long> {

    boolean existsByUserAndRestaurant(User user, Restaurant restaurant);
    boolean existsByUserIdAndRestaurantId(Long userId, long restaurantId);
//    boolean existsByUserIdAndRestaurant_PlaceId(Long userId, String placeId);
    @Query("SELECT COUNT(f) > 0 FROM Favourite f JOIN f.restaurant r WHERE f.user.id = :userId AND r.placeId = :placeId")
    boolean existsByUserIdAndRestaurantPlaceId(@Param("userId") Long userId, @Param("placeId") String placeId);

//    void deleteByUserAndRestaurant(User user, Restaurant restaurant);
    @Transactional
    @Modifying
    @Query("DELETE FROM Favourite f WHERE f.user.id = :userId AND f.restaurant.id = (SELECT r.id FROM Restaurant r WHERE r.placeId = :placeId)")
//@Query("SELECT COUNT(f) > 0 FROM Favourite f JOIN f.restaurant r WHERE f.user.id = :userId AND r.placeId = :placeId")
    void deleteByUserIdAndRestaurantPlaceId(@Param("userId") Long userId, @Param("placeId") String placeId);
}
