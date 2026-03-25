package com.gyu.engdu.domain.engdu.application;

import com.gyu.engdu.domain.engdu.infra.dto.GenerateEngduPartMessage;

public interface EngduMessagePublisher {

    void publish(GenerateEngduPartMessage message);
}
