package com.example.onlinelibrary.service.impl;

import com.example.onlinelibrary.domain.Author;
import com.example.onlinelibrary.domain.Book;
import com.example.onlinelibrary.dto.author.AuthorAllInfoDto;
import com.example.onlinelibrary.dto.author.AuthorCreateDto;
import com.example.onlinelibrary.dto.author.AuthorInfoDto;
import com.example.onlinelibrary.repository.AuthorDao;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceImplTest {

    @InjectMocks
    private AuthorServiceImpl authorService;

    @Mock
    private RestTemplate externalApiClient;

    @Mock
    private AuthorDao authorDao;

    @BeforeEach
    void setUp() {
        authorService = new AuthorServiceImpl(authorDao, externalApiClient);
    }

    @AfterEach
    void tearDown() {
        authorDao.deleteAll();
    }

    @Test
    void createAuthor() {
        AuthorCreateDto authorCreateDto = AuthorCreateDto.builder().firstName("Oleg").lastName("Shulga").country("Austria")
                .dateOfBirth(LocalDate.parse("2000-01-01")).build();
        when(externalApiClient.getForObject(Mockito.anyString(), eq(String.class))).thenReturn("success");
        Set<Book> books = new HashSet<>();
        Author author = new Author(1L, "Oleg", "Shulga", LocalDate.parse("2000-01-01"), "Austria", books);
        when(authorDao.save(ArgumentMatchers.any(Author.class))).thenReturn(author);
        AuthorInfoDto infoDto = authorService.createAuthor(authorCreateDto);
        assertEquals(author.getIdAuthor(), infoDto.getId());
        assertEquals(authorCreateDto.getFirstName(), author.getFirstName());
    }

    @Test
    void updateAuthor() {
        AuthorCreateDto authorCreateDto = AuthorCreateDto.builder().firstName("Oleg").lastName("Shulga").country("Austria")
                .dateOfBirth(LocalDate.parse("2000-01-01")).build();
        when(externalApiClient.getForObject(Mockito.anyString(), eq(String.class))).thenReturn("success");
        Set<Book> books = new HashSet<>();
        Author author = new Author(1L, "Oleg", "Shulga", LocalDate.parse("2000-01-01"), "Austria", books);
        when(authorDao.save(ArgumentMatchers.any(Author.class))).thenReturn(author);
        when(authorDao.getAuthorById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(author));
        AuthorInfoDto infoDto = authorService.updateAuthor(1L, authorCreateDto);
        assertEquals(author.getIdAuthor(), infoDto.getId());
        assertEquals(authorCreateDto.getFirstName(), author.getFirstName());
    }

    @Test
    void getAuthorById() {
        Set<Book> books = new HashSet<>();
        Author author = new Author(1L, "Oleg", "Shulga", LocalDate.parse("2000-01-01"), "Austria", books);
        when(authorDao.getAuthorById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(author));
        AuthorAllInfoDto allInfoA = authorService.getAuthorById(1L);
        assertEquals(author.getFirstName(), allInfoA.getFirstName());
    }

    @Test
    void deleteById() {
        Set<Book> books = new HashSet<>();
        Author author = new Author(1L, "Oleg", "Shulga", LocalDate.parse("2000-01-01"), "Austria", books);
        when(authorDao.getAuthorById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(author));
        authorService.deleteById(1L);
        verify(authorDao).deleteById(ArgumentMatchers.any(Long.class));
    }

    @Test
    void getAllAuthors() {
        Set<Book> books = new HashSet<>();
        Author author1 = new Author(1L, "Oleg", "Shulga", LocalDate.parse("2000-01-01"), "Austria", books);
        Author author2 = new Author(2L, "Sasha", "Shulga", LocalDate.parse("2001-01-01"), "Austria", books);
        List<Author> list = new ArrayList<>(List.of(author1, author2));
        when(authorDao.getAllAuthors(ArgumentMatchers.any(PageRequest.class))).thenReturn(list);
        List<AuthorAllInfoDto> allInfoAuthors = authorService.getAllAuthors(1);
        assertThat(author1.getFirstName()).isEqualTo(allInfoAuthors.get(0).getFirstName());
        assertThat(author2.getFirstName()).isEqualTo(allInfoAuthors.get(1).getFirstName());
    }
}