package com.example.onlinelibrary.web;

import com.example.onlinelibrary.dto.book.BookAllInfoDto;
import com.example.onlinelibrary.dto.favorites.FavoritesCreateDto;
import com.example.onlinelibrary.dto.favorites.FavoritesInfoDto;
import com.example.onlinelibrary.service.FavoritesService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/online-library/v1/favorites")
@AllArgsConstructor
public class FavoritesController {

    private final FavoritesService favoritesService;

    @GetMapping(params = {"userId", "page_num"})
    @ResponseStatus(HttpStatus.OK)
    public List<BookAllInfoDto> getFavorites(Long userId, int page_num) {
        return favoritesService.getFavoritesByUserId(userId, page_num);
    }

    @PostMapping("/add")
    @ResponseStatus(HttpStatus.OK)
    public FavoritesInfoDto addFavorites(@Valid @RequestBody FavoritesCreateDto favoritesDto) {
        return favoritesService.addFavorites(favoritesDto);
    }

    @DeleteMapping(params = {"userId", "bookId"})
    @ResponseStatus(HttpStatus.OK)
    public FavoritesInfoDto deleteBookFromFavorites(Long userId, Long bookId) {
        return favoritesService.deleteBookFromFavoritesByUserId(userId, bookId);
    }
}
