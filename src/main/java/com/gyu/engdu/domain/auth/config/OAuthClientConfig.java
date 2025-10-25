package com.gyu.engdu.domain.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class OAuthClientConfig {

  @Bean(value = "googleCodeClient")
  public RestClient googleCodeRestClient() {
    return RestClient.builder()
        .baseUrl("https://oauth2.googleapis.com")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
        .build();
  }

  @Bean(value = "googleAccessTokenClient")
  public RestClient googleAccessTokenClient() {
    return RestClient.builder()
        .baseUrl("https://www.googleapis.com")
        .build();
  }
}
