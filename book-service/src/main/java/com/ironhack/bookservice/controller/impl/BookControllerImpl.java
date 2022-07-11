package com.ironhack.bookservice.controller.impl;

import com.ironhack.bookservice.client.BookFormatClient;
import com.ironhack.bookservice.controller.dto.BookDto;
import com.ironhack.bookservice.controller.dto.BookFormatDto;
import com.ironhack.bookservice.controller.interfaces.BookController;
import com.ironhack.bookservice.model.Book;
import com.ironhack.bookservice.repository.BookRepository;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Optional;

@RestController
public class BookControllerImpl implements BookController {
    @Autowired
    private IBookService bookService;
    @Autowired
    private BookRepository bookRepository;

    @GetMapping("/book/{isbn}")
    @ResponseStatus(HttpStatus.OK)
    public BookDto getBook (@PathVariable("isbn") String isbn) {

        return bookService.getBook(isbn);
    }

    @PostMapping("/book")
    @ResponseStatus(HttpStatus.CREATED)
    public Book registerNewBook(@RequestBody @Valid BookDto bookDto){

        return bookService.registerNewBook(bookDto);
    }




}
