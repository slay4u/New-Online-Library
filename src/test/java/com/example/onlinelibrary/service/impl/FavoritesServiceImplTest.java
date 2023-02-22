package com.example.onlinelibrary.service.impl;

import com.example.onlinelibrary.domain.*;
import com.example.onlinelibrary.dto.book.BookAllInfoDto;
import com.example.onlinelibrary.dto.favorites.FavoritesCreateDto;
import com.example.onlinelibrary.dto.favorites.FavoritesInfoDto;
import com.example.onlinelibrary.repository.BookDao;
import com.example.onlinelibrary.repository.FavoritesDao;
import com.example.onlinelibrary.repository.UserDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FavoritesServiceImplTest {
    @InjectMocks
    private FavoritesServiceImpl favoritesService;

    @Mock
    private FavoritesDao favoritesDao;

    @Mock
    private UserDao userDao;

    @Mock
    private BookDao bookDao;

    @BeforeEach
    void setUp() {
        favoritesService = new FavoritesServiceImpl(favoritesDao, userDao, bookDao);
    }

    @AfterEach
    void tearDown() {
        favoritesDao.deleteAll();
        userDao.deleteAll();
        bookDao.deleteAll();
    }

    @Test
    void addFavorites() {
        User user = new User(1L, "ggseg", "hthh", "sf11@gmail.com", null, true, Role.USER);
        Author author = new Author();
        Set<Author> authors = new HashSet<>(Set.of(author));
        Set<Genre> genreSet = new HashSet<>(Set.of(Genre.NARRATIVE));
        List<ImageData> imageDataList = new ArrayList<>();
        Book book = new Book(1L, "rdh", LocalDate.now(), authors, Book.Language.ARABIC, "grrge", genreSet, imageDataList);
        Favorites favorites = new Favorites(new Favorites.FavoritesId(user, book));
        Set<Long> set = new HashSet<>(Set.of(book.getIdBook()));
        FavoritesCreateDto fav = FavoritesCreateDto.builder().bookIds(set).idUser(1L).build();
        when(userDao.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(user));
        when(bookDao.getBookById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(book));
        FavoritesInfoDto favInf = favoritesService.addFavorites(fav);
        assertEquals(favInf.getIdUser(), fav.getIdUser());
    }

    @Test
    void getFavoritesByUserId() {
        User user = new User(1L, "ggseg", "hthh", "sf11@gmail.com", null, true, Role.USER);
        Author author = new Author();
        Set<Author> authors = new HashSet<>(Set.of(author));
        Set<Genre> genreSet = new HashSet<>(Set.of(Genre.NARRATIVE));
        List<ImageData> imageDataList = new ArrayList<>();
        Book book = new Book(1L, "rdh", LocalDate.now(), authors, Book.Language.ARABIC, "grrge", genreSet, imageDataList);
        Favorites favorites = new Favorites(new Favorites.FavoritesId(user, book));
        when(userDao.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(user));
        when(bookDao.getFavoritesByUserId(ArgumentMatchers.any(Long.class), ArgumentMatchers.any(PageRequest.class))).thenReturn(List.of(book));
        List<BookAllInfoDto> list = favoritesService.getFavoritesByUserId(1L, 0);
        assertEquals(list.get(0).getName(), book.getName());
    }

    @Test
    void deleteBookFromFavoritesByUserId() {
        User user = new User(1L, "ggseg", "hthh", "sf11@gmail.com", null, true, Role.USER);
        Author author = new Author();
        Set<Author> authors = new HashSet<>(Set.of(author));
        Set<Genre> genreSet = new HashSet<>(Set.of(Genre.NARRATIVE));
        List<ImageData> imageDataList = new ArrayList<>();
        Book book = new Book(1L, "rdh", LocalDate.now(), authors, Book.Language.ARABIC, "grrge", genreSet, imageDataList);
        Favorites favorites = new Favorites(new Favorites.FavoritesId(user, book));
        when(userDao.findById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(user));
        when(bookDao.getBookById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(book));
        when(favoritesDao.findById(ArgumentMatchers.any(Favorites.FavoritesId.class))).thenReturn(Optional.of(favorites));
        FavoritesInfoDto favInf = favoritesService.deleteBookFromFavoritesByUserId(1L, 1L);
        verify(favoritesDao).deleteById(ArgumentMatchers.any());
    }
}