package com.example.onlinelibrary.dto.author;

import com.example.onlinelibrary.util.LocalDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Builder
@Jacksonized
public class AuthorCreateDto {

    @NotBlank(message = "first name can't be empty")
    private String firstName;

    @NotNull(message = "last name can't be empty")
    private String lastName;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @NotNull(message = "country can't be empty")
    private String country;
}
