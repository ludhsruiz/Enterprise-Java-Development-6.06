package com.ironhack.bookService.service.interfaces;

import com.ironhack.bookService.controller.dto.BookDto;
import com.ironhack.bookService.model.Book;

public interface IBookService {

    public BookDto getBook(String isbn);

    public Book registerNewBook(BookDto bookDto);
}
