package com.example.onlinelibrary.service.impl;

import com.example.onlinelibrary.domain.Author;
import com.example.onlinelibrary.dto.author.AuthorAllInfoDto;
import com.example.onlinelibrary.dto.author.AuthorCreateDto;
import com.example.onlinelibrary.dto.author.AuthorInfoDto;
import com.example.onlinelibrary.exception.AlreadyExistException;
import com.example.onlinelibrary.exception.NotFoundException;
import com.example.onlinelibrary.repository.AuthorDao;
import com.example.onlinelibrary.service.AuthorService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final int PAGE_ELEMENTS_AMOUNT = 15;

    private final AuthorDao authorDao;

    private final RestTemplate externalApiClient;

    @Override
    public AuthorInfoDto createAuthor(AuthorCreateDto authorDto) {
        validateAuthor(authorDto);
        Author author = convertToEntity(authorDto, new Author());
        authorDao.save(author);
        return convertEntityToDto(author, "created");
    }

    @Override
    public AuthorInfoDto updateAuthor(Long id, AuthorCreateDto authorDto) {
        validateAuthor(authorDto);
        Author author = convertToEntity(authorDto, new Author());
        Author savedAuthor = authorDao.save(updateContent(author, getById(id)));
        return convertEntityToDto(savedAuthor, "updated");
    }

    @Override
    public AuthorAllInfoDto getAuthorById(Long id) {
        Author byId = getById(id);
        return allInfoDto(byId);
    }

    @Override
    public void deleteById(Long id) {
        getById(id);
        authorDao.deleteById(id);
    }

    @Override
    public List<AuthorAllInfoDto> getAllAuthors(int pageNumber) {
        if(pageNumber < 0) {
            throw new IllegalArgumentException("Page number cannot be less than 0!");
        }
        List<Author> allAuthors = authorDao.getAllAuthors(
                PageRequest.of(pageNumber,
                               PAGE_ELEMENTS_AMOUNT)
        );
        return listToDto(allAuthors);
    }

    private void validateAuthor(AuthorCreateDto author) {
        validatePresentAuthor(author);

        if(author.getFirstName().isBlank() || author.getLastName().isBlank()
                || Objects.isNull(author.getFirstName())
                || Objects.isNull(author.getLastName())) {
            throw new IllegalArgumentException("Author's name is not valid");
        }

        if(author.getCountry().isBlank() || Objects.isNull(author.getCountry())) {
            throw new IllegalArgumentException("Author's country is not valid");
        }

        if(author.getDateOfBirth().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Author's date of birth is not valid");
        }

        validateCountryName(author.getCountry());
    }

    private void validateCountryName(String name) {
        try {
            externalApiClient.getForObject("/name/" + name + "?fullText=true", String.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Country name does not exist" +
                    " or server was not able to validate the name " + name + " !");
        }
    }

    private void validatePresentAuthor(AuthorCreateDto authorDto) {
        String firstName = authorDto.getFirstName();
        String lastName = authorDto.getLastName();
        LocalDate dateOfBirth = authorDto.getDateOfBirth();
        String country = authorDto.getCountry();

        Optional<Author> result = authorDao
                .getAuthorByAllFields(firstName, lastName, dateOfBirth, country);

        if(result.isPresent()) {
            String body = """
          {
              "message": "The author already exist!",
              "firstName": "%s",
              "lastName": "%s",
              "dateOfBirth": "%s",
              "country": "%s"
          }
          """.formatted(firstName, lastName, dateOfBirth, country);
            throw new AlreadyExistException(body);
        }
    }
    private Author updateContent(Author author, Author resultAuthor) {
        resultAuthor.setFirstName(author.getFirstName());
        resultAuthor.setLastName(author.getLastName());
        resultAuthor.setDateOfBirth(author.getDateOfBirth());
        resultAuthor.setCountry(author.getCountry());
        return resultAuthor;
    }

    private Author convertToEntity(AuthorCreateDto authorDto, Author author) {
        author.setFirstName(authorDto.getFirstName());
        author.setLastName(authorDto.getLastName());
        author.setDateOfBirth(authorDto.getDateOfBirth());
        author.setCountry(authorDto.getCountry());
        return author;
    }

    private AuthorInfoDto convertEntityToDto(Author author, String state) {
        return AuthorInfoDto.builder()
                .id(author.getIdAuthor())
                .result("Author "
                        + author.getFirstName()
                        + " " + author.getLastName()
                        + " " + state + " successfully!")
                .build();
    }

    private Author getById(Long id) {
        Optional<Author> resultAuthor = authorDao.getAuthorById(id);
        if(resultAuthor.isEmpty()) {
            throw new NotFoundException("Author by id is not found!");
        }
        return resultAuthor.get();
    }

    private AuthorAllInfoDto allInfoDto(Author author) {
        return new AuthorAllInfoDto(
                author.getFirstName(),
                author.getLastName(),
                author.getDateOfBirth(),
                author.getCountry()
        );
    }

    private List<AuthorAllInfoDto> listToDto(List<Author> authors) {
        return authors.stream().map(this::allInfoDto).collect(Collectors.toList());
    }
}
