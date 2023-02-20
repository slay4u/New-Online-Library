package com.example.onlinelibrary.web;

import com.example.onlinelibrary.dto.book.BookAllInfoDto;
import com.example.onlinelibrary.dto.book.BookCreateDto;
import com.example.onlinelibrary.dto.book.BookInfoDto;
import com.example.onlinelibrary.service.BookService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/online-library/v1/books")
@AllArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public BookInfoDto createBook(@Valid @RequestBody BookCreateDto requestToSave) {
        return bookService.createBook(requestToSave);
    }

    @GetMapping(params = {"page_num"})
    @ResponseStatus(HttpStatus.OK)
    public List<BookAllInfoDto> getAllBooks(int page_num) {
        return bookService.getAllBooks(page_num);
    }

    @GetMapping("/byId/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookAllInfoDto getBookById(@PathVariable("id") Long id) {
        return bookService.getBookById(id);
    }

    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BookInfoDto updateBookById(@Valid @PathVariable("id") Long id,
                                      @RequestBody BookCreateDto requestToSave) {
        return bookService.updateBook(id, requestToSave);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBookById(@PathVariable("id") Long id) {
        bookService.deleteById(id);
    }

    @PostMapping("/photo/upload/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<?> uploadImage(@PathVariable("id") Long id,
                                         @RequestParam ("image")MultipartFile file) throws IOException {
        String uploadImage = bookService.uploadImage(file, id);
        return ResponseEntity.ok(uploadImage);
    }

    @GetMapping(value = "/photo/download/{id}", produces="application/zip")
    @ResponseStatus(HttpStatus.OK)
    public byte[] downloadImage(@PathVariable Long id, HttpServletResponse response) throws IOException {
        response.addHeader("Content-Disposition", "attachment; filename=\"assigned_imgs.zip\"");
        return bookService.downloadImages(id);
    }
}
