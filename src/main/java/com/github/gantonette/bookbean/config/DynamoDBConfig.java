//package com.github.gantonette.bookbean.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
//
//@Configuration
////@EnableDynamoDBRepositories(basePackages = "com.github.gantonette.bookbeandev")
//public class DynamoDBConfig {
//
//    @Value("${aws.accessKeyId}")
//    private String amazonAWSAccessKey;
//
//    @Value("${aws.secretKey}")
//    private String amazonAWSSecretKey;
//
//    @Bean
//    public DynamoDbClient getDynamoDbClient() {
//        return DynamoDbClient.builder()
//                .region(Region.AP_SOUTHEAST_2)
//                .credentialsProvider(StaticCredentialsProvider.create(
//                        AwsBasicCredentials.create(amazonAWSAccessKey, amazonAWSSecretKey)))
//                .build();
//    }
//
//    @Bean
//    public DynamoDbEnhancedClient getDynamoDbEnhancedClient() {
//        return DynamoDbEnhancedClient.builder()
//                .dynamoDbClient(getDynamoDbClient())
//                .build();
//    }
//}
//
//
//
//
//
//
//
//
//
