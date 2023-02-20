package com.example.onlinelibrary.dto.author;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Getter
public class AuthorInfoDto {
    private String result;
    private Long id;
}
