package com.tang.demo_db.service;

import com.tang.demo_db.dao.FavouriteDAO;
import com.tang.demo_db.entity.Favourite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FavouriteService {

    @Autowired
    private FavouriteDAO favouriteDAO;

    public List<Favourite> getAllFavourites() {
        return favouriteDAO.findAllFavourites();
    }

    public Favourite getFavouriteById(Long id) {
        return favouriteDAO.findFavouriteById(id);
    }

    public void createFavourite(Favourite favourite) {
        favouriteDAO.saveFavourite(favourite);
    }

    public void updateFavourite(Favourite favourite) {
        favouriteDAO.updateFavourite(favourite);
    }

    public void deleteFavourite(Long id) {
        favouriteDAO.deleteFavourite(id);
    }
}
