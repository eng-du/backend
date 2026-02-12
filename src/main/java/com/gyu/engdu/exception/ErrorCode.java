package com.gyu.engdu.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // User
  USER_NOT_FOUND("USER-001", "사용자가 존재하지 않습니다."),
  USER_NAME_TOO_LONG("USER-002", "이름은 30자를 초과할 수 없습니다."),

  // Auth
  JWT_INVALID("AUTH-001", "토큰의 형식이 알맞지 않습니다."),
  JWT_EXPIRED("AUTH-002", "만료된 토큰입니다."),
  JWT_NOT_PROVIDED("AUTH-003", "토큰이 입력되지 않았습니다"),
  GOOGLE_4XX("AUTH-004", "해당 서비스가 구글과 연결중에 문제가 발생했습니다."),
  GOOGLE_5XX("AUTH-005", "해당 서비스가 구글과 연결중에 문제가 발생했습니다."),
  INVALID_ID_TOKEN("AUTH-006", "유효하지않은 ID Token 입니다."),
  ACCESS_DENIED("AUTH-007", "권한이 없습니다."),
  REFRESH_TOKEN_NOT_FOUND("AUTH-008", "리프레시 토큰이 존재하지 않습니다."),
  REFRESH_TOKEN_EARLY_ACCESS("AUTH-009", "리프레시 토큰이 예상보다 빨리 접근되었습니다."),

  // Engdu
  ENGDU_GENERATE_4XX("ENGDU-001", "LLM API 연결에 문제가 발생했습니다."),
  ENGDU_GENERATE_5XX("ENGDU-002", "LLM API 연결에 문제가 발생했습니다."),
  ENGDU_NOT_FOUND("ENGDU-003", "잉듀가 존재하지 않습니다."),
  ENGDU_FORBIDDEN_ACCESS("ENGDU-004", "사용자의 잉듀가 아닙니다."),
  ENGDU_LIKE_ALREADY_PROCESSED("ENGDU-005", "이미 좋아요 상태가 결정되었습니다."),
  ENGDU_TITLE_TOO_LONG("ENGDU-006", "제목은 50자를 초과할 수 없습니다."),

  // Question
  QUESTION_FORBIDDEN_ACCESS("QUESTION-001", "해당 잉듀가 갖고있는 문제가 아닙니다."),
  QUESTION_ALREADY_SOLVED("QUESTION-002", "이미 해결한 문제입니다."),

  // Learning
  PHRASAL_NOT_FOUND("LEARNING-001", "구동사가 존재하지 않습니다."),

  // Spring Standard Errors
  INVALID_INPUT_VALUE("COMMON-001", "잘못된 입력값입니다."),
  METHOD_NOT_ALLOWED("COMMON-002", "지원하지 않는 HTTP 메서드입니다."),
  RESOURCE_NOT_FOUND("COMMON-003", "요청한 리소스를 찾을 수 없습니다."),

  // Other Error
  UNKNOWN_ERROR("UNKNOWN-001", "알 수 없는 에러입니다.");

  private final String code;
  private final String message;
}
