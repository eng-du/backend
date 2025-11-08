package com.gyu.engdu.domain.engdu.domain;

import com.gyu.engdu.domain.engdu.domain.enums.Category;
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

  private Byte answer;

  private String content;

  private Boolean isCorrected = false;

  @Enumerated(EnumType.STRING)
  private Category category;

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
  private List<Choice> choices = new ArrayList<>();

  private Question(Byte answer, String content, Category category, Engdu engdu) {
    this.answer = answer;
    this.content = content;
    this.category = category;
    setEngdu(engdu);
  }

  public static Question of(Byte answer, String content, Category category, Engdu engdu) {
    return new Question(answer, content, category, engdu);
  }

  public void setEngdu(Engdu engdu) {
    this.engdu = engdu;
    engdu.getQuestions().add(this);
  }
}
