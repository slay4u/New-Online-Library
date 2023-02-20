package com.example.onlinelibrary.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "favorites")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Favorites {

    @EmbeddedId
    FavoritesId id;

    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FavoritesId implements Serializable {

        @ManyToOne
        @JoinColumn(name = "user_id")
        private User user;

        @ManyToOne
        @JoinColumn(name = "book_id")
        private Book book;
    }
}
