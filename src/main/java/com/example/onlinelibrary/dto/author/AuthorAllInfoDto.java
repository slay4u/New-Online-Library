package com.example.onlinelibrary.dto.author;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class AuthorAllInfoDto {
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String country;
}
