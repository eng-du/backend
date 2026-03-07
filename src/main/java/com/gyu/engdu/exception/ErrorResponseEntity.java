package com.gyu.engdu.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
@Getter
// TODO: ProblemDetail로 개선하기
public class ErrorResponseEntity {
  private int status;
  private String code;
  private String message;

  public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode errorCode, HttpStatus httpStatus) {
    return ResponseEntity
        .status(httpStatus)
        .body(ErrorResponseEntity.of(errorCode, httpStatus));
  }

  public static ErrorResponseEntity of(ErrorCode errorCode, HttpStatus httpStatus) {
    return new ErrorResponseEntity(httpStatus.value(), errorCode.getCode(), errorCode.getMessage());
  }
}