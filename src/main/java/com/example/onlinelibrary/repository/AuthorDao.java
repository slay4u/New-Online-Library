package com.example.onlinelibrary.repository;

import com.example.onlinelibrary.domain.Author;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorDao extends JpaRepository<Author, Long> {

    @Query(value = "SELECT * FROM public.author", nativeQuery = true)
    List<Author> getAllAuthors(Pageable pageable);

    @Query(value = "SELECT * FROM public.author" +
            " WHERE id_author = ?1", nativeQuery = true)
    Optional<Author> getAuthorById(Long id);

    @Query(value = "SELECT * FROM public.author" +
            " WHERE first_name = ?1 AND last_name = ?2" +
            " AND date_of_birth = ?3 AND country = ?4", nativeQuery = true)
    Optional<Author> getAuthorByAllFields(String firstName,
                                          String lastName,
                                          LocalDate dateOfBirth,
                                          String country);
}
