package com.gyu.engdu.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  //User

  //AUTH,
  JWT_INVALID(HttpStatus.UNAUTHORIZED,"AUTH-001","토큰의 형식이 알맞지 않습니다."),
  JWT_EXPIRED(HttpStatus.UNAUTHORIZED,"AUTH-002","만료된 토큰입니다."),
  JWT_NOT_PROVIDED(HttpStatus.UNAUTHORIZED,"AUTH-003","토큰이 입력되지 않았습니다"),
  GOOGLE_4XX(HttpStatus.BAD_REQUEST,"AUTH-004","해당 서비스가 구글과 연결중에 문제가 발생했습니다."),
  GOOGLE_5XX(HttpStatus.INTERNAL_SERVER_ERROR,"AUTH-005","해당 서비스가 구글과 연결중에 문제가 발생했습니다."),
  INVALID_ID_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR,"AUTH-006","유효하지않은 ID Token 입니다."),
  ACCESS_DENIED(HttpStatus.FORBIDDEN,"AUTH-007","권한이 없습니다."),
  REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND,"AUTH-008","리프레시 토큰이 존재하지 않습니다."),
  REFRESH_TOKEN_EARLY_ACCESS(HttpStatus.INTERNAL_SERVER_ERROR,"AUTH-009","리프레시 토큰이 예상보다 빨리 접근되었습니다."),

  //Other Error
  UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"UNKNOWN-001","알 수 없는 에러입니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
