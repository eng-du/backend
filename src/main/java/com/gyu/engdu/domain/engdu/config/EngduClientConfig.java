package com.gyu.engdu.domain.engdu.config;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class EngduClientConfig {

  @Value("${spring.api.gpt-key}")
  private String apiKey;

  @Bean(value = "engduClient")
  public RestClient engduRestClient() {
    HttpClient jdk = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(5))
        .build();

    //응답까지 90초의 타임아웃 시간을 둔다.
    JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory(jdk);
    factory.setReadTimeout(Duration.ofSeconds(500));

    return RestClient.builder()
        .baseUrl("https://api.openai.com/v1")
        .requestFactory(factory)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.apiKey)
        .build();
  }
}
