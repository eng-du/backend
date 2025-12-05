package com.gyu.engdu.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gyu.engdu.exception.ErrorCode;
import com.gyu.engdu.exception.ErrorResponseEntity;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  private final ObjectMapper mapper = new ObjectMapper();

  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {

    ErrorCode errorCode = (ErrorCode) request.getAttribute("errorCode");

    if (errorCode != null) {
      ErrorResponseEntity errorResponse = ErrorResponseEntity.of(errorCode);
      response.setStatus(errorCode.getHttpStatus().value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.setCharacterEncoding("UTF-8");
      response.getWriter().write(mapper.writeValueAsString(errorResponse));
    }
  }
}