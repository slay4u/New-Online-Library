package com.example.onlinelibrary.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Getter
public class BookInfoDto {
    private String result;
    private Long id;
}
