package com.gyu.engdu.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  protected ResponseEntity<ErrorResponseEntity> handleNotFoundException(NotFoundException e) {
    log.error("NotFoundException 발생: {}", e.getDetailMessage(), e);
    return ErrorResponseEntity.toResponseEntity(e.getErrorCode(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ValidationException.class)
  protected ResponseEntity<ErrorResponseEntity> handleValidationException(ValidationException e) {
    log.error("ValidationException 발생: {}", e.getDetailMessage(), e);
    return ErrorResponseEntity.toResponseEntity(e.getErrorCode(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AuthException.class)
  protected ResponseEntity<ErrorResponseEntity> handleAuthException(AuthException e) {
    log.error("AuthException 발생: {}", e.getDetailMessage(), e);
    return ErrorResponseEntity.toResponseEntity(e.getErrorCode(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(ForbiddenException.class)
  protected ResponseEntity<ErrorResponseEntity> handleForbiddenException(ForbiddenException e) {
    log.error("ForbiddenException 발생: {}", e.getDetailMessage(), e);
    return ErrorResponseEntity.toResponseEntity(e.getErrorCode(), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(ExternalServiceException.class)
  protected ResponseEntity<ErrorResponseEntity> handleExternalServiceException(ExternalServiceException e) {
    log.error("ExternalServiceException 발생: {}", e.getDetailMessage(), e);
    return ErrorResponseEntity.toResponseEntity(e.getErrorCode(), HttpStatus.BAD_GATEWAY);
  }

  @ExceptionHandler(InternalServerException.class)
  protected ResponseEntity<ErrorResponseEntity> handleInternalServerException(InternalServerException e) {
    log.error("InternalServerException 발생: {}", e.getDetailMessage(), e);
    return ErrorResponseEntity.toResponseEntity(e.getErrorCode(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // Spring 표준 예외 처리
  @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
  protected ResponseEntity<ErrorResponseEntity> handleMethodArgumentNotValid(
      org.springframework.web.bind.MethodArgumentNotValidException e) {
    log.warn("Validation 에러: {}", e.getBindingResult().getAllErrors());
    return ErrorResponseEntity.toResponseEntity(ErrorCode.INVALID_INPUT_VALUE, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(org.springframework.web.servlet.NoHandlerFoundException.class)
  protected ResponseEntity<ErrorResponseEntity> handleNoHandlerFound(
      org.springframework.web.servlet.NoHandlerFoundException e) {
    log.warn("존재하지 않는 엔드포인트: {}", e.getRequestURL());
    return ErrorResponseEntity.toResponseEntity(ErrorCode.RESOURCE_NOT_FOUND, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
  protected ResponseEntity<ErrorResponseEntity> handleMethodNotSupported(
      org.springframework.web.HttpRequestMethodNotSupportedException e) {
    log.warn("지원하지 않는 HTTP 메서드: {}", e.getMethod());
    return ErrorResponseEntity.toResponseEntity(ErrorCode.METHOD_NOT_ALLOWED, HttpStatus.METHOD_NOT_ALLOWED);
  }

  @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
  protected ResponseEntity<ErrorResponseEntity> handleHttpMessageNotReadable(
      org.springframework.http.converter.HttpMessageNotReadableException e) {
    log.warn("JSON 파싱 실패: {}", e.getMessage());
    return ErrorResponseEntity.toResponseEntity(ErrorCode.INVALID_INPUT_VALUE, HttpStatus.BAD_REQUEST);
  }

  // 기타 예외 처리
  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ErrorResponseEntity> handleException(Exception e) {
    log.error("알 수 없는 예외 발생 {}", e.toString(), e);
    return ErrorResponseEntity.toResponseEntity(ErrorCode.UNKNOWN_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}