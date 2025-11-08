package com.gyu.engdu.domain.engdu.domain;

import jakarta.persistence.Column;
import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Choice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "choice_id")
  private Long id;

  private String content;

  private String explanation;

  private Byte seq;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  private Question question;

  private Choice(String content, String explanation, Byte seq, Question question) {
    this.content = content;
    this.explanation = explanation;
    this.seq = seq;
    setQuestion(question);
  }

  public static Choice of(String content, String explanation, Byte seq, Question question) {
    return new Choice(content, explanation, seq, question);
  }

  public void setQuestion(Question question) {
    this.question = question;
    question.getChoices().add(this);
  }
}
