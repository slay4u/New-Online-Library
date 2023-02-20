package com.example.onlinelibrary.service;

import com.example.onlinelibrary.dto.author.AuthorAllInfoDto;
import com.example.onlinelibrary.dto.author.AuthorCreateDto;
import com.example.onlinelibrary.dto.author.AuthorInfoDto;

import java.util.List;

public interface AuthorService {

    /**
     * Creates new author and persists it to db
     * @param author author to save
     * @return saved author
     */
    AuthorInfoDto createAuthor(AuthorCreateDto author);

    /**
     * Updates an existing author
     * @param id id of author to update
     * @param author content to update
     * @return updated author
     */
    AuthorInfoDto updateAuthor(Long id, AuthorCreateDto author);

    /**
     * Returns an author by id
     * @param id id of author to return
     * @return found author (if id exist)
     */
    AuthorAllInfoDto getAuthorById(Long id);

    /**
     * Deletes an author by id
     * @param id id of author to delete
     */
    void deleteById(Long id);

    /**
     * Returns all the authors from db
     * @param pageNumber number of page with authors
     * @return list of authors
     */
    List<AuthorAllInfoDto> getAllAuthors(int pageNumber);
}
