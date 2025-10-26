package com.gyu.engdu.domain.engdu.presentation;

import com.gyu.engdu.domain.engdu.application.CreateEngduService;
import com.gyu.engdu.domain.engdu.presentation.dto.response.CreateEngduRequest;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/engdu")
@RequiredArgsConstructor
public class EngduController {

  private final CreateEngduService createEngduService;

  @PostMapping
  public ResponseEntity<Void> createUser(@RequestBody CreateEngduRequest request) {
    String topic = request.topic();
    String level = request.level();
    Long userId = createEngduService.create(topic, level);
    URI location = URI.create("/api/v1/engdu/%d".formatted(userId));

    return ResponseEntity.created(location).build();
  }
}
