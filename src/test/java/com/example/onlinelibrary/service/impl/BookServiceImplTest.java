package com.example.onlinelibrary.service.impl;

import com.example.onlinelibrary.domain.Author;
import com.example.onlinelibrary.domain.Book;
import com.example.onlinelibrary.domain.Genre;
import com.example.onlinelibrary.dto.book.BookAllInfoDto;
import com.example.onlinelibrary.dto.book.BookCreateDto;
import com.example.onlinelibrary.dto.book.BookInfoDto;
import com.example.onlinelibrary.repository.AuthorDao;
import com.example.onlinelibrary.repository.BookDao;
import com.example.onlinelibrary.repository.ImageDataDao;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @InjectMocks
    private BookServiceImpl underTest;
    @Mock
    private BookDao bookDao;
    @Mock
    private AuthorDao authorDao;
    @Mock
    private ImageDataDao imageDataDao;

    @BeforeEach
    void setUp() throws URISyntaxException {
        underTest = new BookServiceImpl(bookDao, authorDao, imageDataDao, null, null);
    }

    @Test
    @DisplayName("Testing createBook()")
    void createBook() {
       Author author = new Author(
               1L,
               "John",
               "Liberto",
               LocalDate.parse("2000-10-10"),
               "Austria",
               null);

       when(authorDao.getAuthorById(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.of(author));

        Set<Long> authorsId = new HashSet<>(Set.of(author.getIdAuthor()));

        BookCreateDto bookCreateDto = BookCreateDto.builder()
                .name("Book")
                .publishDate(LocalDate.parse("2000-10-10"))
                .authorsId(authorsId)
                .genres(Set.of("COOKBOOK"))
                .language("UKRAINIAN")
                .desc("desc")
                .build();

        Set<Author> authors = new HashSet<>(Set.of(author));
        Set<Genre> genres = new HashSet<>(Set.of(Genre.COOKBOOK));

        Book savedBook = new Book(
                1L,
                bookCreateDto.getName(),
                bookCreateDto.getPublishDate(),
                authors,
                Book.Language.UKRAINIAN,
                "desc",
                genres,
                null);

        when(bookDao.save(ArgumentMatchers.any(Book.class))).thenReturn(savedBook);
        BookInfoDto infoBook = underTest.createBook(bookCreateDto);
        assertEquals(savedBook.getIdBook(), infoBook.getId());
        assertEquals(bookCreateDto.getName(), savedBook.getName());
    }

    @Test
    @DisplayName("Testing updateBook()")
    void updateBook() {
        Author author = new Author(
                1L,
                "John",
                "Liberto",
                LocalDate.parse("2000-10-10"),
                "Austria",
                null);

        when(authorDao.getAuthorById(ArgumentMatchers.any(Long.class)))
                .thenReturn(Optional.of(author));

        Set<Long> authorsId = new HashSet<>(Set.of(author.getIdAuthor()));

        BookCreateDto bookCreateDto = BookCreateDto.builder()
                .name("Book")
                .publishDate(LocalDate.parse("2000-10-10"))
                .authorsId(authorsId)
                .language("UKRAINIAN")
                .desc("desc")
                .genres(Set.of("COOKBOOK"))
                .build();

        Set<Author> authors = new HashSet<>(Set.of(author));
        Set<Genre> genres = new HashSet<>(Set.of(Genre.COOKBOOK));

        Book savedBook = new Book(
                1L,
                bookCreateDto.getName(),
                bookCreateDto.getPublishDate(),
                authors,
                Book.Language.UKRAINIAN,
                "desc",
                genres,
                null);

        when(bookDao.save(ArgumentMatchers.any(Book.class))).thenReturn(savedBook);
        when(bookDao.getBookById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(savedBook));
        BookInfoDto infoBook = underTest.updateBook(1L, bookCreateDto);
        assertEquals(savedBook.getIdBook(), infoBook.getId());
        assertEquals(bookCreateDto.getName(), savedBook.getName());
    }

    @Test
    @DisplayName("Testing getBookById()")
    void getBookById() {
        Author author = new Author(
                1L,
                "John",
                "Liberto",
                LocalDate.parse("2000-10-10"),
                "Austria",
                null);

        Set<Author> authors = new HashSet<>(Set.of(author));

        Book savedBook = new Book(
                1L,
                "Book",
                LocalDate.parse("2000-10-10"),
                authors,
                Book.Language.UKRAINIAN,
                "desc",
                Set.of(Genre.COOKBOOK),
                null);

        when(bookDao.getBookById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(savedBook));
        BookAllInfoDto allInfoBook = underTest.getBookById(1L);
        assertEquals(savedBook.getName(), allInfoBook.getName());
    }

    @Test
    @DisplayName("Testing deleteById()")
    void deleteById() {
        Author author = new Author(
                1L,
                "John",
                "Liberto",
                LocalDate.parse("2000-10-10"),
                "Austria",
                null);

        Set<Author> authors = new HashSet<>(Set.of(author));

        Book savedBook = new Book(
                1L,
                "Book",
                LocalDate.parse("2000-10-10"),
                authors,
                Book.Language.UKRAINIAN,
                "desc",
                Set.of(Genre.COOKBOOK),
                null);

        when(bookDao.getBookById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(savedBook));
        underTest.deleteById(1L);
        verify(bookDao).deleteById(ArgumentMatchers.any(Long.class));
    }

    @Test
    @DisplayName("Testing getAllBooks()")
    void getAllBooks() {
        Author author = new Author(
                1L,
                "John",
                "Liberto",
                LocalDate.parse("2000-10-10"),
                "Austria",
                null);

        Set<Author> authors = new HashSet<>(Set.of(author));

        Book savedBook1 = new Book(
                1L,
                "Book1",
                LocalDate.parse("2000-10-10"),
                authors,
                Book.Language.UKRAINIAN,
                "desc",
                Set.of(Genre.COOKBOOK),
                null);
        Book savedBook2 = new Book(
                2L,
                "Book2",
                LocalDate.parse("2000-10-10"),
                authors,
                Book.Language.UKRAINIAN,
                "desc",
                Set.of(Genre.NARRATIVE),
                null);

        List<Book> listBooks = new ArrayList<>(List.of(savedBook1, savedBook2));

        when(bookDao.getAllBooks(ArgumentMatchers.any(PageRequest.class))).thenReturn(listBooks);
        List<BookAllInfoDto> allInfoBooks = underTest.getAllBooks(1);
        assertThat(savedBook1.getName()).isEqualTo(allInfoBooks.get(0).getName());
        assertThat(savedBook2.getName()).isEqualTo(allInfoBooks.get(1).getName());
    }

    @Test
    @DisplayName("Testing getAllBooks() if page number less than 0")
    void getAllBooks_withIllegalPageNumber() {
        assertThatThrownBy(() -> underTest.getAllBooks(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Page number cannot be less than 0!");
        verify(bookDao, never()).getAllBooks(any());
    }

    @Test
    void uploadImage() throws IOException {
//        Author author = new Author(
//                1L,
//                "John",
//                "Liberto",
//                LocalDate.parse("2000-10-10"),
//                "Austria",
//                null);
//
//        Set<Author> authors = new HashSet<>(Set.of(author));
//
//        Book savedBook = new Book(
//                1L,
//                "Book1",
//                LocalDate.parse("2000-10-10"),
//                authors,
//                Book.Language.UKRAINIAN,
//                "desc",
//                Set.of(Genre.COOKBOOK),
//                null);
//
//        String filePath = "";
//        MultipartFile multipartFile = new MockMultipartFile("",
//                new FileInputStream(filePath));
//
//        when(bookDao.getBookById(ArgumentMatchers.any(Long.class))).thenReturn(Optional.of(savedBook));
//        String result = underTest.uploadImage(multipartFile, 1L);
//        assertThat(result).isEqualTo("Image uploaded successfully " + multipartFile.getOriginalFilename());
    }

    @Test
    @Disabled
    void downloadImages() {
    }
}