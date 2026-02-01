package com.gyu.engdu.domain.engdu.domain;

import com.gyu.engdu.domain.BaseEntity;
import com.gyu.engdu.domain.engdu.domain.enums.LikeStatus;
import com.gyu.engdu.exception.CustomException;
import com.gyu.engdu.exception.ErrorCode;
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

  @Enumerated(EnumType.STRING)
  private LikeStatus likeStatus;

  private boolean isAllSolved;

  @OneToMany(mappedBy = "engdu", cascade = CascadeType.ALL)
  private List<Question> questions = new ArrayList<>();

  @OneToMany(mappedBy = "engdu", cascade = CascadeType.ALL)
  private List<Article> articles = new ArrayList<>();

  @Builder
  private Engdu(Long userId, String title, String topic, boolean isAllSolved,
      LikeStatus likeStatus) {
    this.userId = userId;
    this.title = title;
    this.topic = topic;
    this.isAllSolved = isAllSolved;
    this.likeStatus = likeStatus;
  }

  public static Engdu of(Long userId, String title, String topic) {
    return Engdu.builder()
        .userId(userId)
        .title(title)
        .topic(topic)
        .isAllSolved(false)
        .likeStatus(LikeStatus.NONE)
        .build();
  }

  public void validateOwner(Long userId) {
    if (!this.userId.equals(userId)) {
      throw new CustomException(ErrorCode.ENGDU_FORBIDDEN_ACCESS);
    }
  }

  public boolean submission(Long questionId, Byte userAnswer) {
    Question question = findQuestion(questionId);
    boolean isAnswered = question.solve(userAnswer);
    if (isAnswered) {
      increaseSolvedCount();
    }
    return isAnswered;
  }

  public void changeLikeStatus(LikeStatus likeStatus) {
    if (this.likeStatus != LikeStatus.NONE) {
      throw new CustomException(ErrorCode.ENGDU_LIKE_ALREADY_PROCESSED);
    }
    this.likeStatus = likeStatus;
  }

  private Question findQuestion(Long questionId) {
    return getQuestions().stream()
        .filter(q -> q.getId().equals(questionId))
        .findFirst()
        .orElseThrow(() -> new CustomException(ErrorCode.QUESTION_FORBIDDEN_ACCESS));
  }

  private void increaseSolvedCount() {
    this.solvedCount++;

    if (this.solvedCount == this.questions.size()) {
      this.isAllSolved = Boolean.TRUE;
    }
  }
}
