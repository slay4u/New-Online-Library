package com.example.onlinelibrary.dto.favorites;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Getter
public class FavoritesInfoDto {
    private String result;
    private Long idUser;
}
