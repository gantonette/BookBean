package com.github.gantonette.bookbean.service;

import com.github.gantonette.bookbean.model.Book;
import com.github.gantonette.bookbean.model.BookEntry;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;


@Service
public class BookService {
    Logger logger = Logger.getLogger(BookService.class.getName());

    @Autowired DynamoDbTemplate dynamoDbTemplate;
    @Autowired BookEntryService bookEntryService;

    public List<Book> getBooks() {
        final ScanEnhancedRequest request = ScanEnhancedRequest.builder().build();

        PageIterable<Book> result = dynamoDbTemplate.scan(request, Book.class);

        List<Book> books = new ArrayList<>();

        logger.info("result: " + result);

        result.items().forEach(books::add);

        return books;
    }

    public Book getBook(String id) {
        QueryConditional query =
                QueryConditional.keyEqualTo(k -> k.partitionValue(id));

        QueryEnhancedRequest request = QueryEnhancedRequest.builder().queryConditional(query).build();

        PageIterable<Book> result = dynamoDbTemplate.query(request, Book.class);

        List<Book> books = new ArrayList<>();

        result.items().forEach(books::add);

        if (books.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }

        return books.get(0);
    }

    public Book postBook(Book book) {
        String id = UUID.randomUUID().toString();

        book.setId(id);
        dynamoDbTemplate.save(book);
        return book;
    }

    public Book updateBook(String id, Book book) {
        Book currentBook = getBook(id);

        currentBook.setTitle(book.getTitle());
        currentBook.setAuthor(book.getAuthor());

        dynamoDbTemplate.update(currentBook);
        return currentBook;
    }

    public void deleteBook(String id) {
        dynamoDbTemplate.delete(
                Key.builder().partitionValue(id).build(), Book.class
        );

        List<BookEntry> bookEntries = bookEntryService.getBookEntries(id);

        for(BookEntry bookentry : bookEntries) {
            bookEntryService.deleteBookEntry(bookentry.getBookEntryId());
        }
    }

}

