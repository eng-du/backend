package com.gyu.engdu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EngDuApplication {

  public static void main(String[] args) {
    SpringApplication.run(EngDuApplication.class, args);
  }

}
