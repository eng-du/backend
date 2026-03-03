package com.gyu.engdu.domain.engdu.application;

import com.gyu.engdu.domain.engdu.infra.dto.EngduSqsMessage;

public interface EngduMessagePublisher {

    void publish(EngduSqsMessage message);
}
