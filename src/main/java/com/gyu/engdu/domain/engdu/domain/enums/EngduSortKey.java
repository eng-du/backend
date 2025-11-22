package com.gyu.engdu.domain.engdu.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EngduSortKey {
  CREATED_AT("createdAt");

  private final String property;
}
