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
public class Article {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "article_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "engdu_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  private Engdu engdu;

  @Column(length = 2000)
  private String content;

  @Column(length = 2000)
  private String translation;

  private Article(String content, String translation, Engdu engdu) {
    this.content = content;
    this.translation = translation;
    setEngdu(engdu);
  }

  public static Article of(String content, String translation, Engdu engdu) {
    return new Article(content, translation, engdu);
  }

  public void setEngdu(Engdu engdu) {
    this.engdu = engdu;
    engdu.getArticles().add(this);
  }
}
