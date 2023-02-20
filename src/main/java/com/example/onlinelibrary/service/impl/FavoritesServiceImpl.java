package com.example.onlinelibrary.service.impl;

import com.example.onlinelibrary.domain.Book;
import com.example.onlinelibrary.domain.Favorites;
import com.example.onlinelibrary.domain.User;
import com.example.onlinelibrary.dto.book.BookAllInfoDto;
import com.example.onlinelibrary.dto.favorites.FavoritesCreateDto;
import com.example.onlinelibrary.dto.favorites.FavoritesInfoDto;
import com.example.onlinelibrary.exception.NotFoundException;
import com.example.onlinelibrary.repository.BookDao;
import com.example.onlinelibrary.repository.FavoritesDao;
import com.example.onlinelibrary.repository.UserDao;
import com.example.onlinelibrary.service.FavoritesService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class FavoritesServiceImpl implements FavoritesService, BookGeneralHandler {

    private final int PAGE_ELEMENTS_AMOUNT = 15;
    private final FavoritesDao favoritesDao;
    private final UserDao userDao;
    private final BookDao bookDao;

    @Override
    public FavoritesInfoDto addFavorites(FavoritesCreateDto favoritesDto) {
        User targetUser = getUserById(favoritesDto.getIdUser());
        Set<Book> favBooks = validateBookIds(favoritesDto.getBookIds());
        Favorites favorites = setFavourites(targetUser, favBooks, new Favorites());
        favoritesDao.save(favorites);
        return FavoritesInfoDto.builder()
                .result("Books were successfully added to favorites!")
                .idUser(targetUser.getUserId())
                .build();
    }

    @Override
    public List<BookAllInfoDto> getFavoritesByUserId(Long userId, int pageNum) {
        if(pageNum < 0) {
            throw new IllegalArgumentException("Page number cannot be less than 0!");
        }
        getUserById(userId);
        List<Book> favoritesByUserId =
                bookDao.getFavoritesByUserId(userId,
                                                  PageRequest.of(pageNum, PAGE_ELEMENTS_AMOUNT));
        return listToDto(favoritesByUserId);
    }

    @Override
    public FavoritesInfoDto deleteBookFromFavoritesByUserId(Long userId, Long bookId) {
        User targetUser = getUserById(userId);
        Book bookToDelete = bookDao.getBookById(bookId)
                .orElseThrow(() -> new NotFoundException("Book with id "
                        + bookId + " has not been found!"));
        Favorites.FavoritesId favId = new Favorites.FavoritesId(targetUser, bookToDelete);
        validateFavId(favId);
        favoritesDao.deleteById(favId);
        return FavoritesInfoDto.builder()
                .result("Book with id " + bookId + " was successfully deleted from favorites!")
                .idUser(targetUser.getUserId())
                .build();
    }

    private User getUserById(Long userId) {
        return userDao.findById(userId).orElseThrow(() ->
                new NotFoundException("User with id " + userId + " has not been found!"));
    }

    private Set<Book> validateBookIds(Set<Long> bookIds) {
        return bookIds.stream().map(id -> bookDao.getBookById(id)
                .orElseThrow(() -> new NotFoundException("Book with id "
                        + id + " has not been found!"))).collect(Collectors.toSet());
    }

    private Favorites setFavourites(User targetUser, Set<Book> favBooks, Favorites favorites) {
        favBooks.forEach(book -> favorites.setId(new Favorites.FavoritesId(targetUser, book)));
        return favorites;
    }

    private void validateFavId(Favorites.FavoritesId favId) {
        favoritesDao.findById(favId)
                .orElseThrow(() -> new NotFoundException("Fav id has not been found (possible deleted)"));
    }
}
