package com.gyu.engdu.domain.engdu.infra;

import com.gyu.engdu.domain.engdu.application.dto.request.GenerateEngduRequest;
import com.gyu.engdu.domain.engdu.domain.enums.PartType;
import java.util.Map;

public interface PromptGenerator {

    PartType getPartType();

    Map<String, Object> generatePrompt(GenerateEngduRequest request);
}
