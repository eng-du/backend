package com.gyu.engdu.domain.engdu.presentation.dto.response;

import java.util.List;
import org.springframework.data.domain.Page;

public record EngduPageResponse<T>(
    List<T> content,
    int totalPages,
    boolean hasEngdu) {

  public static <T> EngduPageResponse<T> from(Page<T> page, boolean hasEngdu) {
    return new EngduPageResponse<>(
        page.getContent(),
        page.getTotalPages(),
        hasEngdu
    );
  }
}
