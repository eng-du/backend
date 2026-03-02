package com.gyu.engdu.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Configuration
@Profile("!test")
public class AwsSQSConfig {

  @Value("${spring.cloud.aws.region.static}")
  private String region;

  @Bean
  public SqsAsyncClient sqsAsyncClient() {
    return SqsAsyncClient.builder()
        .region(Region.of(region))
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build();
  }

//  @Bean
//  public SqsAsyncClient sqsAsyncClient() {
//    var builder = SqsAsyncClient.builder()
//        .region(Region.of(region));
//
//    if (profile != null && !profile.isBlank()) {
//      builder.credentialsProvider(ProfileCredentialsProvider.create(profile));
//    } else {
//      builder.credentialsProvider(DefaultCredentialsProvider.create());
//    }
//
//    return builder.build();
//  }

}
