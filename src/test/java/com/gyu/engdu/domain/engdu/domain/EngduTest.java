package com.gyu.engdu.domain.engdu.domain;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.gyu.engdu.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EngduTest {

  @DisplayName("사용자가 잉듀의 소유자가 아니라면, validateOwner 메서드는 예외를 발생시킨다.")
  @Test
  void validateNonOwnerId() {
    //given
    Long engduOwnerId = 1L;
    Long nonOwnerId = 2L;
    Engdu engdu = Engdu.of(engduOwnerId, "test", "test");

    //when & then
    assertThatThrownBy(() -> engdu.validateOwner(nonOwnerId))
        .isInstanceOf(CustomException.class)
        .hasMessage("사용자의 잉듀가 아닙니다.");
  }

  @DisplayName("사용자가 잉듀의 소유자라면, validateOwner 메서드를 통과한다.")
  @Test
  void validateOwner() {
    //given
    Long engduOwnerId = 1L;
    Engdu engdu = Engdu.of(engduOwnerId, "test", "test");

    //when
    engdu.validateOwner(engduOwnerId);

    //then
  }
}