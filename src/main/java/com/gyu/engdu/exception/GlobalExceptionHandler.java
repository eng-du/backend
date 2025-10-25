package com.gyu.engdu.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
  // 커스텀 예외 처리
  @ExceptionHandler(CustomException.class)
  protected ResponseEntity<ErrorResponseEntity> handleCustomException(CustomException e) {
    log.error("커스텀 예외 발생", e);
    return ErrorResponseEntity.toResponseEntity(e.getErrorCode());
  }

  // 기타 예외 처리
  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ErrorResponseEntity> handleCustomException(Exception e) {
    log.error("알 수 없는 예외 발생 {}", e.toString(), e);
    return ErrorResponseEntity.toResponseEntity(ErrorCode.UNKNOWN_ERROR);
  }
}