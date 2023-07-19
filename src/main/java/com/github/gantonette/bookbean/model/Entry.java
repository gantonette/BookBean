package com.github.gantonette.bookbean.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Entry {

    private String bookEntryId;
    private String bookId;
    private String description;
    private String imageRef;

    @DynamoDbPartitionKey
    public String getBookEntryId() {
        return bookEntryId;
    }

    public void setBookEntryId(String bookEntryId) {
        this.bookEntryId = bookEntryId;
    }

}
