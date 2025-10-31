package com.gyu.engdu.domain.engdu.application.dto.request;

public record GenerateEngduRequest(
    String topic,
    String level
) {

  public static GenerateEngduRequest of(String topic, String level) {
    return new GenerateEngduRequest(topic, level);
  }
}
