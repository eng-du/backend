package com.gyu.engdu.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

  private final ErrorCode errorCode;
  private final String detailMessage; // 로깅용 상세 메시지

  public CustomException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.detailMessage = errorCode.getMessage();
  }

  // 컨텍스트 정보를 포함한 생성자
  protected CustomException(ErrorCode errorCode, String detailMessage) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.detailMessage = detailMessage;
  }
}
