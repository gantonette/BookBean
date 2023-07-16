package com.github.gantonette.bookbean.service;

import com.github.gantonette.bookbean.model.Book;
import com.github.gantonette.bookbean.model.BookEntry;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class BookEntryService {

    @Autowired DynamoDbTemplate dynamoDbTemplate;

    public List<BookEntry> getBookEntries(String bookId) {
        ScanEnhancedRequest request;

        if(Objects.equals(bookId, "all")) {
            request = ScanEnhancedRequest.builder().build();
        } else {
            Expression filterExpression = Expression.builder().expression("bookId = :bookId").putExpressionValue(":bookId", AttributeValue.fromS(bookId)).build();
            request = ScanEnhancedRequest.builder().filterExpression(filterExpression).build();
        }

        PageIterable<BookEntry> result = dynamoDbTemplate.scan(request, BookEntry.class);

        List<BookEntry> bookEntryList = new ArrayList<>();

        result.items().forEach(bookEntryList::add);

        return bookEntryList;

    }

    public BookEntry getBookEntry(String entryId) {
        QueryConditional query =
                QueryConditional.keyEqualTo(k -> k.partitionValue(entryId));

        QueryEnhancedRequest request = QueryEnhancedRequest.builder().queryConditional(query).build();

        PageIterable<BookEntry> result = dynamoDbTemplate.query(request, BookEntry.class);

        List<BookEntry> bookEntries = new ArrayList<>();

        result.items().forEach(bookEntries::add);

        if (bookEntries.size() == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }

        return bookEntries.get(0);
    }

    public BookEntry postBookEntry(String bookId, BookEntry bookEntry) {
        bookEntry.setBookId(bookId);
        String entryId = UUID.randomUUID().toString();
        bookEntry.setBookEntryId(entryId);
        dynamoDbTemplate.save(bookEntry);
        return bookEntry;
    }

    public BookEntry updateBookEntry(String entryId, BookEntry bookEntry) {
        BookEntry existingBookEntry = getBookEntry(entryId);

        existingBookEntry.setDescription(bookEntry.getDescription());
        existingBookEntry.setImageRef(bookEntry.getImageRef());
        dynamoDbTemplate.update(existingBookEntry);

        return existingBookEntry;
    }

    public void deleteBookEntry(String entryId) {
        BookEntry existingBookEntry = getBookEntry(entryId);
        dynamoDbTemplate.delete(existingBookEntry);
    }
}
