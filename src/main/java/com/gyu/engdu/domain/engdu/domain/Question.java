package com.gyu.engdu.domain.engdu.domain;

import com.gyu.engdu.domain.engdu.domain.enums.Category;
import com.gyu.engdu.exception.CustomException;
import com.gyu.engdu.exception.ErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Question {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "question_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "engdu_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  private Engdu engdu;

  private byte answer;

  private String content;

  private boolean isCorrected;

  @Enumerated(EnumType.STRING)
  private Category category;

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
  private List<Choice> choices = new ArrayList<>();

  @Builder
  private Question(byte answer, String content, Category category, boolean isCorrected) {
    this.answer = answer;
    this.content = content;
    this.category = category;
    this.isCorrected = isCorrected;
  }

  public static Question of(byte answer, String content, Category category, Engdu engdu) {
    Question question = Question.builder()
        .answer(answer)
        .content(content)
        .category(category)
        .isCorrected(false)
        .build();
    question.setEngdu(engdu);
    return question;
  }

  public void setEngdu(Engdu engdu) {
    this.engdu = engdu;
    engdu.getQuestions().add(this);
  }

  public boolean solve(byte userAnswer) {
    if (this.isCorrected) {
      throw new CustomException(ErrorCode.QUESTION_ALREADY_SOLVED);
    }

    boolean isAnswered = checkAnswer(userAnswer);
    if (isAnswered) {
      markCorrected();
    }
    return isAnswered;
  }

  private boolean checkAnswer(byte userAnswer) {
    return this.answer == userAnswer;
  }

  private void markCorrected() {
    this.isCorrected = true;
  }
}
