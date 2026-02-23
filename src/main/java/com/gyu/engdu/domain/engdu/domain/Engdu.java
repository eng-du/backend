package com.gyu.engdu.domain.engdu.domain;

import com.gyu.engdu.domain.BaseEntity;
import com.gyu.engdu.domain.engdu.domain.enums.LikeStatus;
import com.gyu.engdu.domain.engdu.exception.EngduForbiddenAccessException;
import com.gyu.engdu.domain.engdu.exception.EngduLikeAlreadyProcessedException;
import com.gyu.engdu.domain.engdu.exception.EngduTitleTooLongException;
import com.gyu.engdu.domain.engdu.exception.QuestionForbiddenAccessException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Engdu extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "engdu_id")
  private Long id;

  private Long userId;

  private String title;

  private String topic;

  private int solvedCount = 0;

  private String level;

  @Enumerated(EnumType.STRING)
  private LikeStatus likeStatus;

  private boolean isAllSolved;

  @OneToMany(mappedBy = "engdu", cascade = CascadeType.ALL)
  private List<Part> parts = new ArrayList<>();

  @Builder
  private Engdu(Long userId, String topic, String level, boolean isAllSolved,
      LikeStatus likeStatus) {
    this.userId = userId;
    this.topic = topic;
    this.level = level;
    this.isAllSolved = isAllSolved;
    this.likeStatus = likeStatus;
  }

  public static Engdu of(Long userId, String level, String topic) {
    return Engdu.builder()
        .userId(userId)
        .topic(topic)
        .level(level)
        .isAllSolved(false)
        .likeStatus(LikeStatus.NONE)
        .build();
  }

  public void validateOwner(Long userId) {
    if (!this.userId.equals(userId)) {
      throw new EngduForbiddenAccessException(userId, this.id, this.userId);
    }
  }

  public boolean submission(Long questionId, Byte userAnswer) {
    Part part = findPartContaining(questionId);
    boolean isAnswered = part.solveQuestion(questionId, userAnswer);

    if (isAnswered) {
      this.solvedCount++;
      checkAndMarkAllSolved();
    }

    return isAnswered;
  }

  public void changeLikeStatus(LikeStatus likeStatus) {
    if (this.likeStatus != LikeStatus.NONE) {
      throw new EngduLikeAlreadyProcessedException(this.userId, this.id, this.likeStatus);
    }
    this.likeStatus = likeStatus;
  }

  private Part findPartContaining(Long questionId) {
    return getParts().stream()
        .filter(part -> part.getQuestions().stream()
            .anyMatch(q -> q.getId().equals(questionId)))
        .findFirst()
        .orElseThrow(() -> new QuestionForbiddenAccessException(this.id, questionId));
  }

  private void checkAndMarkAllSolved() {
    boolean allPartsCompleted = this.parts.stream().allMatch(Part::isAllSolved);
    if (allPartsCompleted) {
      this.isAllSolved = Boolean.TRUE;
    }
  }

  public void changeTitle(String title) {
    if (title.length() > 150) {
      throw new EngduTitleTooLongException(this.userId, this.id, title.length());
    }

    this.title = title;
  }
}
