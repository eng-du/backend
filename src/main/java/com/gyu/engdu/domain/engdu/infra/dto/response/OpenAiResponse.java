package com.gyu.engdu.domain.engdu.infra.dto.response;

import java.util.List;

public record OpenAiResponse(List<Output> output) {

  public record Output(List<Chunk> content) {

  }

  public record Chunk(String type, String text) {

  }
}

