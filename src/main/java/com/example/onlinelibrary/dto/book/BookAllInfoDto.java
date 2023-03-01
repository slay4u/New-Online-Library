package com.example.onlinelibrary.dto.book;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDate;
import java.util.Set;

@Builder
@Jacksonized
@Getter
@Setter
public class BookAllInfoDto {
    private String name;
    private String publishDate;
    private Set<String> authors;
    private Set<String> genres;
    private String language;
    private String desc;
    private byte[] image;
}
