package com.example.onlinelibrary.web;

import com.example.onlinelibrary.dto.author.AuthorAllInfoDto;
import com.example.onlinelibrary.dto.author.AuthorCreateDto;
import com.example.onlinelibrary.dto.author.AuthorInfoDto;
import com.example.onlinelibrary.service.AuthorService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/online-library/v1/authors")
@AllArgsConstructor
public class AuthorController {

    private final AuthorService authorService;

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthorInfoDto createAuthor(@RequestBody AuthorCreateDto requestToSave) {
        return authorService.createAuthor(requestToSave);
    }

    @GetMapping(params = {"page_num"})
    @ResponseStatus(HttpStatus.OK)
    public List<AuthorAllInfoDto> getAllAuthors(int page_num) {
        return authorService.getAllAuthors(page_num);
    }

    @GetMapping("/byId/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorAllInfoDto getAuthorById(@PathVariable("id") Long id) {
        return authorService.getAuthorById(id);
    }

    @PutMapping("/update/{id}")
    @ResponseStatus(HttpStatus.OK)
    public AuthorInfoDto updateAuthorById(@PathVariable("id") Long id,
                                          @RequestBody AuthorCreateDto requestToSave) {
        return authorService.updateAuthor(id, requestToSave);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteAuthorById(@PathVariable("id") Long id) {
        authorService.deleteById(id);
    }
}
