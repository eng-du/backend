package com.gyu.engdu.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@AllArgsConstructor
@Getter
//TODO: ProblemDetail로 개선하기
public class ErrorResponseEntity {
  private int status;
  private String code;
  private String message;

  public static ResponseEntity<ErrorResponseEntity> toResponseEntity(ErrorCode e){
    return ResponseEntity
        .status(e.getHttpStatus())
        .body(ErrorResponseEntity.of(e));
  }

  public static ErrorResponseEntity of(ErrorCode e) {
    return new ErrorResponseEntity(e.getHttpStatus().value(), e.getCode(), e.getMessage());
  }
}