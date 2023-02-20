package com.example.onlinelibrary.service;

import com.example.onlinelibrary.dto.book.BookAllInfoDto;
import com.example.onlinelibrary.dto.favorites.FavoritesCreateDto;
import com.example.onlinelibrary.dto.favorites.FavoritesInfoDto;

import java.util.List;

public interface FavoritesService {

    FavoritesInfoDto addFavorites(FavoritesCreateDto favoritesDto);

    List<BookAllInfoDto> getFavoritesByUserId(Long userId, int pageNum);

    FavoritesInfoDto deleteBookFromFavoritesByUserId(Long userId, Long bookId);
}
