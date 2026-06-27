package com.gyu.engdu.domain.engdu.infra;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gyu.engdu.domain.engdu.application.EngduClient;
import com.gyu.engdu.domain.engdu.application.dto.request.GenerateEngduRequest;
import com.gyu.engdu.domain.engdu.application.dto.response.GeneratedEngduResponse;
import com.gyu.engdu.domain.engdu.domain.enums.PartType;
import com.gyu.engdu.domain.engdu.exception.EngduGenerate4xxException;
import com.gyu.engdu.domain.engdu.exception.EngduGenerate5xxException;
import com.gyu.engdu.domain.engdu.infra.dto.response.OpenAiResponse;
import com.gyu.engdu.domain.engdu.exception.UnsupportedPartTypeException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
public class GptEngduClient implements EngduClient {

  private final RestClient engduClient;
  private final ObjectMapper objectMapper;
  private final Map<PartType, PromptGenerator> promptGeneratorMap;

  public GptEngduClient(
      @Qualifier("engduClient") RestClient engduClient,
      ObjectMapper objectMapper,
      List<PromptGenerator> generators) {
    this.engduClient = engduClient;
    this.objectMapper = objectMapper;
    this.promptGeneratorMap = generators.stream()
        .collect(Collectors.toMap(PromptGenerator::getPartType, Function.identity()));
  }

  @SneakyThrows
  @Override
  public GeneratedEngduResponse generateEngdu(GenerateEngduRequest request) {

    log.debug("keyword: {}, step: {} previousContent: {}",
        request.topic(),
        request.step(),
        request.previousArticleContent());

    Map<String, Object> requestBody = buildRequestBody(request);
    OpenAiResponse response = engduClient.post()
        .uri("/responses")
        .accept(MediaType.APPLICATION_JSON)
        .body(requestBody)
        .retrieve()
        .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
          log.error("Engdu Generate 4XX error: {}", res.getStatusCode());
          throw new EngduGenerate4xxException(res.getStatusCode().value());
        })
        .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
          log.error("Engdu Generate 5XX error: {}", res.getStatusCode());
          throw new EngduGenerate5xxException(res.getStatusCode().value());
        })
        .body(OpenAiResponse.class);

    String json = response.output().get(0).content().get(0).text();

    if (response.usage() != null) {
      log.debug("GPT Token Usage - Total: {}, Prompt: {}, Completion: {}",
          response.usage().total_tokens(),
          response.usage().prompt_tokens(),
          response.usage().completion_tokens());
    }

    return objectMapper.readValue(json, GeneratedEngduResponse.class);
  }

  private Map<String, Object> buildRequestBody(GenerateEngduRequest request) {
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("model", "gpt-5.1");

    PromptGenerator generator = promptGeneratorMap.get(request.step());
    if (generator == null) {
      throw new UnsupportedPartTypeException(request.step());
    }

    Map<String, Object> promptData = generator.generatePrompt(request);
    requestBody.putAll(promptData);

    return requestBody;
  }
}
