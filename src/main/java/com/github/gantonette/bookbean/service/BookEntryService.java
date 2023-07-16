package com.github.gantonette.bookbean.service;

import com.github.gantonette.bookbean.model.Book;
import com.github.gantonette.bookbean.model.BookEntry;
import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import io.awspring.cloud.s3.S3Resource;
import io.awspring.cloud.s3.S3Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.ScanEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class BookEntryService {

    private final String BUCKET_NAME = "book-entry-files";
    @Autowired DynamoDbTemplate dynamoDbTemplate;
    @Autowired S3Template s3Template;

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
        bookEntry.setImageRef(null);
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

    public ResponseEntity<InputStreamResource> getBookEntryImage(String entryId) {
        try {
            BookEntry existingBookEntry = getBookEntry(entryId);
            if (existingBookEntry.getImageRef() == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "image does not exist");
            }
            S3Resource image = s3Template.download(BUCKET_NAME, existingBookEntry.getImageRef());

            HttpHeaders headers = new HttpHeaders();
            InputStreamResource streamSource = new InputStreamResource(image.getInputStream());
            headers.setContentLength(image.contentLength());
            headers.setContentType(MediaType.valueOf(image.contentType()));
            return new ResponseEntity<>(streamSource, headers, HttpStatus.OK);
        } catch (ResponseStatusException | IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book entry not found");
        }
    }

    public void addBookEntryImage(String entryId, MultipartFile image) {
        try {
            BookEntry existingBookEntry = getBookEntry(entryId);
            String imageRef = existingBookEntry.getBookEntryId() + "-" + image.getOriginalFilename();
            s3Template.upload(BUCKET_NAME, imageRef, image.getInputStream());
            existingBookEntry.setImageRef(imageRef);
            dynamoDbTemplate.update(existingBookEntry);
        } catch (ResponseStatusException | IOException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book not found");
        }

    }
    public void deleteBookEntryImage(String entryId) {
        try {
            BookEntry existingBookEntry = getBookEntry(entryId);
            if (existingBookEntry.getImageRef() == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Image not found");
            }
            s3Template.deleteObject(BUCKET_NAME, existingBookEntry.getImageRef());
            existingBookEntry.setImageRef(null);
            dynamoDbTemplate.update(existingBookEntry);
        } catch (ResponseStatusException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Book entry not found");
        }
    }
}
