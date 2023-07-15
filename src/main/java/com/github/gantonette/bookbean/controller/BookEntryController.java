package com.github.gantonette.bookbean.controller;

import com.github.gantonette.bookbean.model.BookEntry;
import com.github.gantonette.bookbean.service.BookEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookEntryController {

    @Autowired
    BookEntryService bookEntryService;

    @GetMapping("/books/{bookId}/entries")
    List<BookEntry> getBookEntries(@PathVariable String bookId) {
        return bookEntryService.getBookEntries(bookId);
    }

    @GetMapping("/books/{bookId}/entries/{entryId}")
    BookEntry getBookEntry(@PathVariable String bookId, @PathVariable String entryId) {
        return bookEntryService.getBookEntry(entryId);
    }

    @PostMapping("/books/{bookId}/entries")
    BookEntry postBookEntry(@PathVariable String bookId, @RequestBody BookEntry bookEntry) {
        return bookEntryService.postBookEntry(bookId, bookEntry);
    }

    @PutMapping("/books/{bookId}/entries/{entryId}")
    BookEntry updateBookEntry(@PathVariable String bookId, @PathVariable String entryId, @RequestBody BookEntry bookEntry) {
        return null;
    }

    @DeleteMapping("/books/{bookId}/entries/{entryId}")
    void deleteBookEntry(@PathVariable String id) {
    }


}
