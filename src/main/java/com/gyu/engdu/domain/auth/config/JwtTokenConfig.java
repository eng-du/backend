package com.gyu.engdu.domain.auth.config;

import com.gyu.engdu.domain.auth.application.TokenParser;
import com.gyu.engdu.domain.auth.application.TokenProvider;
import com.gyu.engdu.domain.auth.infra.JwtTokenParser;
import com.gyu.engdu.domain.auth.infra.JwtTokenProvider;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtTokenConfig {

  @Value("${spring.jwt.secret-key}")
  private String secretKey;

  @Bean
  public TokenProvider jwtProvider() {
    return new JwtTokenProvider(Keys.hmacShaKeyFor(secretKey.getBytes()));
  }

  @Bean
  public TokenParser jwtParser() {
    return new JwtTokenParser(Keys.hmacShaKeyFor(secretKey.getBytes()));
  }

}
