package com.example.onlinelibrary.dto.book;

import com.example.onlinelibrary.util.LocalDateDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Builder
@Jacksonized
public class BookCreateDto {

    @NotBlank(message = "name can't be empty")
    private String name;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishDate;

    @NotNull(message = "authors ids must be present")
    private Set<Long> authorsId;

    @NotNull(message = "genres must be present")
    private Set<String> genres;
}
