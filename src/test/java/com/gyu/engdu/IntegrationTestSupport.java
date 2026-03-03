package com.gyu.engdu;

import com.gyu.engdu.domain.engdu.application.EngduMessagePublisher;
import com.gyu.engdu.domain.engdu.infra.EngduSqsMessageListener;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ActiveProfiles("test")
@SpringBootTest
public abstract class IntegrationTestSupport {

  @MockitoBean
  EngduMessagePublisher engduMessagePublisher;

  @MockitoBean
  EngduSqsMessageListener engduSqsMessageListener;

}