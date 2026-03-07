package com.gyu.engdu.domain.engdu.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SolvedFilter {
  TRUE(Boolean.TRUE),
  FALSE(Boolean.FALSE),
  ALL(null);

  private final Boolean property;
}
