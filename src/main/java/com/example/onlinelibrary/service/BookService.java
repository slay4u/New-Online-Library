package com.example.onlinelibrary.service;

import com.example.onlinelibrary.dto.book.BookAllInfoDto;
import com.example.onlinelibrary.dto.book.BookCreateDto;
import com.example.onlinelibrary.dto.book.BookInfoDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface BookService {

    /**
     * Creates new book and persists it to db
     * @param book book to save
     * @return saved book
     */
    BookInfoDto createBook(BookCreateDto book);

    /**
     * Updates an existing book
     * @param id id of book to update
     * @param book content to update
     * @return updated book
     */
    BookInfoDto updateBook(Long id, BookCreateDto book);

    /**
     * Returns a book by id
     * @param id id of book to return
     * @return found book (if id exist)
     */
    BookAllInfoDto getBookById(Long id);

    /**
     * Deletes a book by id
     * @param id id of book to delete
     */
    void deleteById(Long id);

    /**
     * Returns all the books from db
     * @param pageNumber number of page with books
     * @return list of books
     */
    List<BookAllInfoDto> getAllBooks(int pageNumber);

    /**
     * Uploads books image (filepath) to db, image to filesystem.
     * @param file img to save
     * @param id book's id to assign
     * @return success message
     * @throws IOException if saving failed
     */
    String uploadImage(MultipartFile file, Long id) throws IOException;

    /**
     * Downloads image by book's id
     * @param id id of book to find image by
     * @return found image
     * @throws IOException if something fails
     */
    byte[] downloadImages(Long id) throws IOException;
}
