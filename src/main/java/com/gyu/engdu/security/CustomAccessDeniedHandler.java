package com.gyu.engdu.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gyu.engdu.exception.ErrorCode;
import com.gyu.engdu.exception.ErrorResponseEntity;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

  ObjectMapper mapper = new ObjectMapper();

  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    ErrorResponseEntity errorResponse = ErrorResponseEntity.of(ErrorCode.ACCESS_DENIED);
    response.setStatus(errorResponse.getStatus());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(mapper.writeValueAsString(errorResponse));
  }
}