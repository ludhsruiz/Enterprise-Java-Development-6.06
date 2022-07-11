package com.ironhack.bookService.service.impl;
import com.ironhack.bookService.client.BookFormatClient;
import com.ironhack.bookService.controller.dto.BookDto;
import com.ironhack.bookService.controller.dto.BookFormatDto;
import com.ironhack.bookService.model.Book;
import com.ironhack.bookService.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;

@Service
public class BookService {

    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private BookFormatClient bookFormatClient;
    @Autowired
    private BookRepository bookRepository;

    private CircuitBreakerFactory circuitBreakerFactory = new Resilience4JCircuitBreakerFactory();

    public BookDto getBook(String isbn) {

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("bookFormat-service");

        String[] url = circuitBreaker.run(()-> bookFormatClient.getBookFormat(isbn), throwable -> bookFormatFallBack(isbn));

        if (url == null){
            System.out.println("Something went wrong. We don't have the book formats available");
        }

        Optional<Book> book = bookRepository.findById(isbn);
        if (book.isPresent()) {
            BookDto bookDto = new BookDto();
            bookDto.setIsbn(book.get().getIsbn());
            bookDto.setTitle(book.get().getTitle());
            bookDto.setAuthor(book.get().getAuthor());
            bookDto.setGenre(book.get().getGenre());
            bookDto.setFormats(url);

            return bookDto;

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "book not found");
        }
    }

    private String[] bookFormatFallBack(String isbn) {
        return null;
    }

    public Book registerNewBook(BookDto bookDto){

        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("bookFormat-service");

        Book book = new Book();
        book.setIsbn(bookDto.getIsbn());
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setGenre(bookDto.getGenre());


        BookFormatDto bookFormatDto = new BookFormatDto(bookDto.getIsbn(), bookDto.getFormats());

        BookDto bookDto1 = circuitBreaker.run(()-> bookFormatClient.createBookFormat(bookFormatDto), throwable -> createBookFormatFallBack(bookFormatDto));

        return bookRepository.save(book);
    }

    private BookDto createBookFormatFallBack(BookFormatDto bookFormatDto) {
        throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Our service is down...");
    }
}
