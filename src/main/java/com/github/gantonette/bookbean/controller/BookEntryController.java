package com.github.gantonette.bookbean.controller;

import com.github.gantonette.bookbean.model.Entry;
import com.github.gantonette.bookbean.service.BookEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
public class BookEntryController {

    @Autowired
    BookEntryService bookEntryService;

    @GetMapping("/books/{bookId}/entries")
    List<Entry> getBookEntries(@PathVariable String bookId) {
        return bookEntryService.getBookEntries(bookId);
    }

    @GetMapping("/books/{bookId}/entries/{entryId}")
    Entry getBookEntry(@PathVariable String bookId, @PathVariable String entryId) {
        return bookEntryService.getBookEntry(entryId);
    }

    @PostMapping("/books/{bookId}/entries")
    Entry postBookEntry(@PathVariable String bookId, @RequestBody Entry entry) {
        return bookEntryService.postBookEntry(bookId, entry);
    }

    @PutMapping("/books/{bookId}/entries/{entryId}")
    Entry updateBookEntry(@PathVariable String bookId, @PathVariable String entryId, @RequestBody Entry entry) {
        return bookEntryService.updateBookEntry(entryId, entry);
    }

    @DeleteMapping("/books/{bookId}/entries/{entryId}")
    void deleteBookEntry(@PathVariable String entryId, @PathVariable String bookId) {
        bookEntryService.deleteBookEntry(entryId);
    }

    @GetMapping("/books/{bookId}/entries/{entryId}/image")
    @ResponseBody
    ResponseEntity<InputStreamResource> getBookEntryImage(@PathVariable String bookId, @PathVariable String entryId) {
        return bookEntryService.getBookEntryImage(entryId);
    }

    @PostMapping("/books/{bookId}/entries/{entryId}/image")
    void addBookEntryImage(@PathVariable String entryId, @PathVariable String bookId, @RequestPart MultipartFile image) {
        bookEntryService.addBookEntryImage(entryId, image);
    }

    @DeleteMapping("/books/{bookId}/entries/{entryId}/image/")
    void deleteBookEntryImage(@PathVariable String entryId, @PathVariable String bookId) {
        bookEntryService.deleteBookEntryImage(entryId);
    }
}
