package com.gyu.engdu.config;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsAopConfig {

  @Bean
  public TimedAspect timedAspect(MeterRegistry meterRegistry) {
    return new TimedAspect(meterRegistry);
  }
}
