package com.example.onlinelibrary.service.impl;

import com.example.onlinelibrary.domain.Author;
import com.example.onlinelibrary.domain.Book;
import com.example.onlinelibrary.domain.Genre;
import com.example.onlinelibrary.domain.ImageData;
import com.example.onlinelibrary.dto.book.BookAllInfoDto;
import com.example.onlinelibrary.dto.book.BookCreateDto;
import com.example.onlinelibrary.dto.book.BookInfoDto;
import com.example.onlinelibrary.exception.AlreadyExistException;
import com.example.onlinelibrary.exception.NotFoundException;
import com.example.onlinelibrary.repository.AuthorDao;
import com.example.onlinelibrary.repository.BookDao;
import com.example.onlinelibrary.repository.ImageDataDao;
import com.example.onlinelibrary.service.BookService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Transactional
public class BookServiceImpl implements BookService, BookGeneralHandler {

    private final int PAGE_ELEMENTS_AMOUNT = 15;

    private final BookDao bookDao;

    private final AuthorDao authorDao;

    private final ImageDataDao imageDataDao;

    private final String FOLDER_PATH;

    public BookServiceImpl(BookDao bookDao,
                           AuthorDao authorDao,
                           ImageDataDao imageDataDao) throws URISyntaxException {
        this.bookDao = bookDao;
        this.authorDao = authorDao;
        this.imageDataDao = imageDataDao;
        this.FOLDER_PATH = "C:\\Users\\vladi\\IdeaProjects\\OnlineLibrary\\src\\main\\resources\\images";
    }

    private String getFOLDER_PATH() {
        URL res = BookServiceImpl.class.getClassLoader().getResource("images");
        assert res != null;
        File file = null;
        try {
            file = Paths.get(res.toURI()).toFile();
        } catch (URISyntaxException e) {
            System.out.println("Exception: " + e);
            throw new RuntimeException(e);
        }
        return file.getAbsolutePath();
    }

    @Override
    public BookInfoDto createBook(BookCreateDto bookDto) {
        validateBook(bookDto);
        validateBookName(bookDto.getName());
        Set<Genre> genres = getGenres(bookDto);
        Set<Author> authors = getAuthorsOrThrow(bookDto.getAuthorsId());
        Book.Language lang = getLanguages(bookDto);
        Book book = convertToEntity(bookDto, authors, genres, lang, new Book());
        Book savedBook = bookDao.save(book);
        return convertEntityToDto(savedBook, "created");
    }

    @Override
    public BookInfoDto updateBook(Long id, BookCreateDto bookDto) {
        validateBook(bookDto);
        Set<Genre> genres = getGenres(bookDto);
        Set<Author> authors = getAuthorsOrThrow(bookDto.getAuthorsId());
        Book.Language lang = getLanguages(bookDto);
        Book book = convertToEntity(bookDto, authors, genres, lang, new Book());
        Book savedBook = bookDao.save(updateContent(book, getById(id)));
        return convertEntityToDto(savedBook, "updated");
    }

    @Override
    public BookAllInfoDto getBookById(Long id) {
        Book byId = getById(id);
        return allInfoDto(byId);
    }

    @Override
    public void deleteById(Long id) {
        getById(id);
        bookDao.deleteById(id);
    }

    @Override
    public List<BookAllInfoDto> getAllBooks(int pageNumber) {
        if(pageNumber < 0) {
            throw new IllegalArgumentException("Page number cannot be less than 0!");
        }
        List<Book> books = bookDao.getAllBooks(PageRequest.of(pageNumber, PAGE_ELEMENTS_AMOUNT));
        return listToDto(books);
    }

    @Override
    public String uploadImage(MultipartFile file, Long id) throws IOException {

        String filePath = FOLDER_PATH + "\\" + file.getOriginalFilename();
        validatePresentImage(file.getOriginalFilename(), filePath);
        Book byId = getById(id);

        imageDataDao.save(ImageData
                .builder()
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .filePath(filePath)
                .book(byId)
                .build()
        );

        file.transferTo(new File(filePath));
        return "Image uploaded successfully " + file.getOriginalFilename();
    }

    @Override
    public byte[] downloadImages(Long id) throws IOException {
        List<ImageData> imageData = imageDataDao.findAllByBookId(id);
        if(imageData.isEmpty()) {
            throw new NotFoundException("No image by the id " + id + " has been found!");
        }
        return zipImages(imageData.stream().map(ImageData::getFilePath).toList());
    }

    private void validateBook(BookCreateDto book) {
        if(book.getName().isBlank() || Objects.isNull(book.getName())) {
            throw new IllegalArgumentException("Book's name is not valid");
        }
        if(book.getPublishDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Publish date is not valid");
        }
    }

    private void validateBookName(String name) {
        Optional<Book> byName = bookDao.getBookByName(name);
        if(byName.isPresent()) {
            throw new IllegalArgumentException("Book with the name "
                    + name +
                    " already exist!");
        }
    }

    private void validateGenres(Set<String> genresInString) {
        A:for(String genreInString : genresInString) {
            for(Genre genre : Genre.values()) {
                if(genre.name().equals(genreInString)) {
                    continue A;
                }
            }
            throw new IllegalArgumentException("Genre was not found!");
        }
    }

    private void validatePresentImage(String name, String filePath) {
        Optional<ImageData> result = imageDataDao.findByNameAndFilePath(name, filePath);
        if(result.isPresent()) {
            throw new AlreadyExistException("Image already exist!");
        }
    }

    private Set<Genre> getGenres(BookCreateDto book) {
        Set<String> genresInString = book.getGenres();
        validateGenres(genresInString);
        return genresInString.stream()
                .map(Genre::valueOf)
                .collect(Collectors.toSet());
    }

    private Book.Language getLanguages(BookCreateDto bookDto) {
        String lang = bookDto.getLanguage();
        for(Book.Language language : Book.Language.values()) {
            if(language.name().equals(lang)) {
                return Book.Language.valueOf(lang);
            }
        }
        throw new IllegalArgumentException("Language was not found!");
    }

    private Book updateContent(Book book, Book resultBook) {
        resultBook.setName(book.getName());
        resultBook.setAuthors(book.getAuthors());
        resultBook.setPublishDate(book.getPublishDate());
        resultBook.setGenreSet(book.getGenreSet());
        return resultBook;
    }

    private Book getById(Long id) {
        Optional<Book> resultBook = bookDao.getBookById(id);
        if(resultBook.isEmpty()) {
            throw new NotFoundException("Book by id was not found!");
        }
        return resultBook.get();
    }

    private Author getAuthorsById(Long id) {
        Optional<Author> resultAuthor = authorDao.getAuthorById(id);
        if(resultAuthor.isEmpty()) {
            throw new NotFoundException("Author by id was not found!");
        }
        return resultAuthor.get();
    }

    private Set<Author> getAuthorsOrThrow(Set<Long> ids) {
        return ids.stream()
                .map(this::getAuthorsById)
                .collect(Collectors.toSet());
    }

    private Book convertToEntity(BookCreateDto bookDto,
                                 Set<Author> authors,
                                 Set<Genre> genres,
                                 Book.Language language,
                                 Book book) {
        book.setName(bookDto.getName());
        book.setPublishDate(bookDto.getPublishDate());
        book.setAuthors(authors);
        book.setGenreSet(genres);
        book.setLanguage(language);
        book.setDescription(bookDto.getDesc());
        return book;
    }

    private BookInfoDto convertEntityToDto(Book book, String state) {
        return BookInfoDto.builder()
                .id(book.getIdBook())
                .result("Book " + book.getName() + " " + state + " successfully")
                .build();
    }

    private byte[] zipImages(List<String> imgPathList) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
        ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

        List<File> fileList = new ArrayList<>();

        for(String imgPath : imgPathList) {
            fileList.add(new File(imgPath));
        }

        for(File file : fileList) {
            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
            FileInputStream fileInputStream;
            try {
                fileInputStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new NotFoundException("Image with path "
                        + file.getPath() + " has not been found!");
            }

            IOUtils.copy(fileInputStream, zipOutputStream);

            fileInputStream.close();
            zipOutputStream.closeEntry();
        }

        zipOutputStream.finish();
        zipOutputStream.flush();
        IOUtils.closeQuietly(zipOutputStream);
        IOUtils.closeQuietly(bufferedOutputStream);
        IOUtils.closeQuietly(byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();
    }
}
