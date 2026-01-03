package com.gyu.engdu.domain.engdu.domain;

import com.gyu.engdu.domain.BaseEntity;
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
public class ArticleChunk extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "article_chunk_id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "article_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
  private Article article;

  @Column(length = 2000)
  private String en;

  @Column(length = 2000)
  private String kor;

  private ArticleChunk(String en, String kor, Article article) {
    this.en = en;
    this.kor = kor;
    setArticle(article);
  }

  public static ArticleChunk of(String en, String kor, Article article) {
    return new ArticleChunk(en, kor, article);
  }

  private void setArticle(Article article) {
    this.article = article;
    article.getChunks().add(this);
  }
}
