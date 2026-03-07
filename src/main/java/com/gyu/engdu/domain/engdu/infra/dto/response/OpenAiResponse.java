package com.gyu.engdu.domain.engdu.infra.dto.response;

import java.util.List;

public record OpenAiResponse(List<Output> output, Usage usage) {

  public record Output(List<Chunk> content) {

  }

  public record Chunk(String type, String text) {

  }

  public record Usage(int total_tokens, int prompt_tokens, int completion_tokens) {

  }
}
