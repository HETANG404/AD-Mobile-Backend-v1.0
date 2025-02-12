package com.tang.demo_db.controller;

import com.tang.demo_db.entity.Favourite;
import com.tang.demo_db.service.FavouriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favourites")
public class FavouriteController {

    @Autowired
    private FavouriteService favouriteService;

    @GetMapping
    public List<Favourite> getAllFavourites() {
        return favouriteService.getAllFavourites();
    }

    @GetMapping("/{id}")
    public Favourite getFavouriteById(@PathVariable Long id) {
        return favouriteService.getFavouriteById(id);
    }

    @PostMapping
    public void createFavourite(@RequestBody Favourite favourite) {
        favouriteService.createFavourite(favourite);
    }

    @PutMapping("/{id}")
    public void updateFavourite(@PathVariable Long id, @RequestBody Favourite favourite) {
        favourite.setId(id);
        favouriteService.updateFavourite(favourite);
    }

    @DeleteMapping("/{id}")
    public void deleteFavourite(@PathVariable Long id) {
        favouriteService.deleteFavourite(id);
    }
}
