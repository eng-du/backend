package com.gyu.engdu.domain.message.application;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gyu.engdu.domain.message.domain.Message;
import com.gyu.engdu.domain.message.domain.MessageRepository;
import com.gyu.engdu.domain.message.domain.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CreateMessageService {

  private final MessageRepository messageRepository;
  private final ObjectMapper objectMapper;

  // 아웃박스 메시지를 생성하고 DB에 저장합니다.
  public void save(MessageType type, Object event) {
    String traceId = MDC.get("traceId");
    Message message = Message.of(type, objectMapper.convertValue(event, JsonNode.class), traceId);
    messageRepository.save(message);
  }
}
