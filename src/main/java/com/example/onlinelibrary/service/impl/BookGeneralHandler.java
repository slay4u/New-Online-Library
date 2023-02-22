package com.example.onlinelibrary.service.impl;

import com.example.onlinelibrary.domain.Book;
import com.example.onlinelibrary.dto.book.BookAllInfoDto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public interface BookGeneralHandler {

    default BookAllInfoDto allInfoDto(Book book) {
        Set<String> authors = book.getAuthors().stream()
                .map(String::valueOf)
                .collect(Collectors.toSet());

        Set<String> genres = book.getGenreSet().stream()
                .map(String::valueOf)
                .collect(Collectors.toSet());

        return new BookAllInfoDto(
                book.getName(),
                book.getPublishDate(),
                authors,
                genres,
                String.valueOf(book.getLanguage()),
                book.getDescription()
        );
    }

    default List<BookAllInfoDto> listToDto(List<Book> books) {
        return books.stream().map(this::allInfoDto).collect(Collectors.toList());
    }
}
