package com.gyu.engdu.domain.message.domain;

import com.gyu.engdu.domain.engdu.infra.dto.GenerateEngduPartMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MessageType {

    GENERATE_ENGDU_PART(GenerateEngduPartMessage.class);

    private final Class<?> payloadClass;
}
