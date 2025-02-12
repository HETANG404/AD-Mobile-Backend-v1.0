package com.tang.demo_db.service;

import com.tang.demo_db.dao.RestaurantDAO;
import com.tang.demo_db.entity.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RestaurantServiceHZY {

    @Autowired
    private RestaurantDAO restaurantDAO;

    public List<Restaurant> getAllRestaurants() {
        return restaurantDAO.findAllRestaurants();
    }

    public Restaurant getRestaurantById(Long id) {
        return restaurantDAO.findRestaurantById(id);
    }

    public void createRestaurant(Restaurant restaurant) {
        restaurantDAO.saveRestaurant(restaurant);
    }

    public void updateRestaurant(Restaurant restaurant) {
        restaurantDAO.updateRestaurant(restaurant);
    }

    public void deleteRestaurant(Long id) {
        restaurantDAO.deleteRestaurant(id);
    }
}
