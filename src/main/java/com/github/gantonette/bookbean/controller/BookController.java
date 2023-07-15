package com.github.gantonette.bookbean.controller;

import com.github.gantonette.bookbean.model.Book;
import com.github.gantonette.bookbean.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookController {

    @Autowired BookService bookService;

    @GetMapping("/")
    String home() {
        return "Hello World!";
    }

    @GetMapping("/books")
    List<Book> getBooks() {
        return bookService.getBooks();
    }

    @GetMapping("/books/{id}")
    Book getBook(@PathVariable String id) {
        return bookService.getBook(id);
    }

}
