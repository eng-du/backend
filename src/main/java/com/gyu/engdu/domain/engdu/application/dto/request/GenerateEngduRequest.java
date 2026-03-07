package com.gyu.engdu.domain.engdu.application.dto.request;

import com.gyu.engdu.domain.engdu.domain.enums.PartType;

public record GenerateEngduRequest(
    String topic,
    String level,
    PartType step,
    String previousArticleContent) {

  public static GenerateEngduRequest of(String topic, String level, PartType step,
      String previousArticleContent) {
    return new GenerateEngduRequest(topic, level, step, previousArticleContent);
  }
}
