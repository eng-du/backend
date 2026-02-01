package com.gyu.engdu.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // User
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-001", "사용자가 존재하지 않습니다."),

  // Auth
  JWT_INVALID(HttpStatus.UNAUTHORIZED, "AUTH-001", "토큰의 형식이 알맞지 않습니다."),
  JWT_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH-002", "만료된 토큰입니다."),
  JWT_NOT_PROVIDED(HttpStatus.UNAUTHORIZED, "AUTH-003", "토큰이 입력되지 않았습니다"),
  GOOGLE_4XX(HttpStatus.BAD_REQUEST, "AUTH-004", "해당 서비스가 구글과 연결중에 문제가 발생했습니다."),
  GOOGLE_5XX(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-005", "해당 서비스가 구글과 연결중에 문제가 발생했습니다."),
  INVALID_ID_TOKEN(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-006", "유효하지않은 ID Token 입니다."),
  ACCESS_DENIED(HttpStatus.FORBIDDEN, "AUTH-007", "권한이 없습니다."),
  REFRESH_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH-008", "리프레시 토큰이 존재하지 않습니다."),
  REFRESH_TOKEN_EARLY_ACCESS(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH-009",
      "리프레시 토큰이 예상보다 빨리 접근되었습니다."),

  // Engdu
  ENGDU_GENERATE_4XX(HttpStatus.BAD_REQUEST, "ENGDU-001", "LLM API 연결에 문제가 발생했습니다."),
  ENGDU_GENERATE_5XX(HttpStatus.INTERNAL_SERVER_ERROR, "ENGDU-002", "LLM API 연결에 문제가 발생했습니다."),
  ENGDU_NOT_FOUND(HttpStatus.NOT_FOUND, "ENGDU-003", "잉듀가 존재하지 않습니다."),
  ENGDU_FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "ENGDU-004", "사용자의 잉듀가 아닙니다."),
  ENGDU_LIKE_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "ENGDU-005", "이미 좋아요 상태가 결정되었습니다."),

  // Question
  QUESTION_FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "QUESTION-001", "해당 잉듀가 갖고있는 문제가 아닙니다."),
  QUESTION_ALREADY_SOLVED(HttpStatus.BAD_REQUEST, "QUESTION-002", "이미 해결한 문제입니다."),

  // Other Error
  UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "UNKNOWN-001", "알 수 없는 에러입니다.");

  private final HttpStatus httpStatus;
  private final String code;
  private final String message;
}
