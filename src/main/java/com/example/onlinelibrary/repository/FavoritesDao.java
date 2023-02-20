package com.example.onlinelibrary.repository;

import com.example.onlinelibrary.domain.Favorites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FavoritesDao extends JpaRepository<Favorites, Favorites.FavoritesId> {

}
