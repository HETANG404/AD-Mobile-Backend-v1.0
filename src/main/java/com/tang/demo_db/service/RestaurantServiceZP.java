package com.tang.demo_db.service;

import com.tang.demo_db.dao.FavouriteDAO;
import com.tang.demo_db.entity.Favourite;
import com.tang.demo_db.entity.Restaurant;
import com.tang.demo_db.entity.User;
import com.tang.demo_db.repository.FavouriteRepository;
import com.tang.demo_db.repository.RestaurantRepository;
import com.tang.demo_db.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
public class RestaurantServiceZP {

    private final ExternalApiService externalApiService;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private final FavouriteRepository favouriteRepository;
    private final FavouriteDAO favouriteDAO;


    public ResponseEntity<String> fetchRecommendedRestaurants(String query) {
        return externalApiService.getRecommendations(query);
    }

    public void addRestaurantToFavorites(String userId, Restaurant restaurant) {
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new RuntimeException("用户未找到"));

        // 先检查是否已收藏（通过 placeId）
        if (favouriteRepository.existsByUserIdAndRestaurantPlaceId(Long.parseLong(userId), restaurant.getPlaceId())) {
            return; // 已收藏，直接返回
        }

        // 如果 placeId 存在，则获取该餐厅（不重复存入）
        Restaurant savedRestaurant = restaurantRepository.findByPlaceId(restaurant.getPlaceId())
                .orElseGet(() -> restaurantRepository.save(restaurant));

//        if (!favouriteRepository.existsByUserAndRestaurant(user, savedRestaurant)) {
//            Favourite favourite = new Favourite();
//            favourite.setUser(user);
//            favourite.setRestaurant(savedRestaurant);
//            favouriteRepository.save(favourite);
//        }
        // 保存收藏
        Favourite favourite = new Favourite();
        favourite.setUser(user);
        favourite.setRestaurant(savedRestaurant);
        favouriteRepository.save(favourite);
    }

    public void removeRestaurantFromFavorites(String userId, String placeId) {
        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new RuntimeException("用户未找到"));

        Restaurant restaurant = restaurantRepository.findByPlaceId(placeId)
                .orElseThrow(() -> new RuntimeException("餐厅未找到"));

//        favouriteRepository.deleteByUserAndRestaurant(user, restaurant);
        // 检查是否已收藏
        if (!favouriteRepository.existsByUserIdAndRestaurantPlaceId(user.getId(), placeId)) {
            return; // 该餐厅未收藏，直接返回
        }

        // 通过 userId 和 placeId 删除收藏
        favouriteRepository.deleteByUserIdAndRestaurantPlaceId(user.getId(), placeId);
    }

    public List<Restaurant> getUserFavoriteRestaurants(String userId) {
        List<Favourite> favourites = favouriteDAO.findFavoritesByUserId(userId);
        return favourites.stream().map(Favourite::getRestaurant).collect(Collectors.toList());
    }

    public boolean isRestaurantFavorited(Long userId, String placeId) {
        return favouriteRepository.existsByUserIdAndRestaurantPlaceId(userId, placeId);
    }

}

