package com.tang.demo_db.dao;

import com.tang.demo_db.entity.Favourite;
import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Repository
public class FavouriteDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Favourite> findAllFavourites() {
        return entityManager.createQuery("SELECT f FROM Favourite f", Favourite.class).getResultList();
    }

    public Favourite findFavouriteById(Long id) {
        return entityManager.find(Favourite.class, id);
    }

    public void saveFavourite(Favourite favourite) {
        entityManager.persist(favourite);
    }

    public void updateFavourite(Favourite favourite) {
        entityManager.merge(favourite);
    }

    public void deleteFavourite(Long id) {
        Favourite favourite = entityManager.find(Favourite.class, id);
        if (favourite != null) {
            entityManager.remove(favourite);
        }
    }
    public List<Favourite> findFavoritesByUserId(String userId) {
        return entityManager.createQuery(
                        "SELECT f FROM Favourite f WHERE f.user.id = :userId", Favourite.class)
                .setParameter("userId", userId)
                .getResultList();
    }
}
