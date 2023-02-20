package com.example.onlinelibrary.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Set;

@AllArgsConstructor
@Getter
public class BookAllInfoDto {
    private String name;
    private LocalDate publishDate;
    private Set<String> authors;
    private Set<String> genres;
}
