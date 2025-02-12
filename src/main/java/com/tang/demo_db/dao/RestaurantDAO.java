package com.tang.demo_db.dao;

import com.tang.demo_db.entity.Restaurant;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Restaurant> findAllRestaurants() {
        return entityManager.createQuery("SELECT r FROM Restaurant r", Restaurant.class).getResultList();
    }

    public Restaurant findRestaurantById(Long id) {
        return entityManager.find(Restaurant.class, id);
    }

    public void saveRestaurant(Restaurant restaurant) {
        entityManager.persist(restaurant);
    }

    public void updateRestaurant(Restaurant restaurant) {
        entityManager.merge(restaurant);
    }

    public void deleteRestaurant(Long id) {
        Restaurant restaurant = entityManager.find(Restaurant.class, id);
        if (restaurant != null) {
            entityManager.remove(restaurant);
        }
    }
}
