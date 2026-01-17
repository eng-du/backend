package com.gyu.engdu.domain.user.application;

import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NameUserService {

  private final Random random = new Random();
  private final List<String> ANIMALS = List.of(
       "코알라", "쿼카", "웜뱃",
      "나무늘보", "다람쥐", "햄스터", "캥거루",
      "돌고래", "꿀벌", "카피바라",
      "고슴도치", "오리너구리", "벌꿀오소리"
  );
  private final List<String> ADJECTIVES = List.of(
      "똑똑한", "호기심많은", "보들보들한", "알록달록한", "매끄러운",
      "비밀스러운", "다정한", "울퉁불퉁한", "자신감있는"
  );

  public String getRandomName() {
    int animalIdx = random.nextInt(ANIMALS.size());
    int adjectiveIdx = random.nextInt(ADJECTIVES.size());
    String animal = ANIMALS.get(animalIdx);
    String adjective = ADJECTIVES.get(adjectiveIdx);
    String name = "%s %s".formatted(adjective, animal);
    log.debug("사용자가 회원가입 했습니다. 회원의 자동 생성 이름: {}", name);
    return name;
  }

}
