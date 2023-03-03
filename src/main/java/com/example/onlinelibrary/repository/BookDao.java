package com.example.onlinelibrary.repository;

import com.example.onlinelibrary.domain.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookDao extends JpaRepository<Book, Long> {

    @Query(value = "SELECT * FROM public.book", nativeQuery = true)
    List<Book> getAllBooks(Pageable pageable);
    
    @Query(value = "SELECT * FROM public.book" +
            " WHERE name=?1", nativeQuery = true)
    Optional<Book> getBookByName(String name);

    @Query(value = "SELECT * FROM public.book" +
            " WHERE id_book = ?1", nativeQuery = true)
    Optional<Book> getBookById(Long id);

    @Query(value = "SELECT b.* FROM public.favorites " +
            "INNER JOIN book b on b.id_book = favorites.book_id " +
            "INNER JOIN usert u on u.user_id = favorites.user_id " +
            "WHERE u.user_id = ?1", nativeQuery = true)
    List<Book> getFavoritesByUserId(Long idUser, Pageable pageable);

    @Query(value = "SELECT COUNT(*) FROM public.book",
            nativeQuery = true)
    long getAllBookCount();
}
