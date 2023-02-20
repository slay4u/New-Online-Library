package com.example.onlinelibrary.dto.favorites;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Builder
@Jacksonized
public class FavoritesCreateDto {

    @NotNull(message = "BookIds not specified")
    private Set<Long> bookIds;

    @NotNull(message = "idUser must be present")
    private Long idUser;
}
