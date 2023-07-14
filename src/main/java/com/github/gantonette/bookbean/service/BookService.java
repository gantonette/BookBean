package com.github.gantonette.bookbean.service;

import com.github.gantonette.bookbean.model.BookObject;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@Service
public class BookService {
    Logger logger = Logger.getLogger(BookService.class.getName());

    @Autowired DynamoDbTemplate dynamoDbTemplate;

    public List<BookObject> getBooks() {
        final ScanEnhancedRequest request = ScanEnhancedRequest.builder().build();

        PageIterable<BookObject> result = dynamoDbTemplate.scan(request, BookObject.class);

        List<BookObject> bookObjects = new ArrayList<>();

        logger.info("result: " + result);

        result.items().forEach(bookObjects::add);

        return bookObjects;
    }

    public BookObject getBook(String id) {
        QueryConditional query =
                QueryConditional.keyEqualTo(k -> k.partitionValue(id));
        QueryEnhancedRequest request =
                QueryEnhancedRequest.builder().queryConditional(query).build();

        PageIterable<BookObject> result = dynamoDbTemplate.query(request, BookObject.class);

        List<BookObject> bookObjects = new ArrayList<>();

        result.items().forEach(bookObjects::add);

        if (bookObjects.size() > 0) {
            return bookObjects.get(0);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }
    }

}

