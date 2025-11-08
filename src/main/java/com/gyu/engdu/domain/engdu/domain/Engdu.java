package com.gyu.engdu.domain.engdu.domain;

import com.gyu.engdu.exception.CustomException;
import com.gyu.engdu.exception.ErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Engdu {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "engdu_id")
  private Long id;

  private Long userId;

  private String title;

  private String topic;

  @OneToMany(mappedBy = "engdu", cascade = CascadeType.ALL)
  private List<Question> questions = new ArrayList<>();

  @OneToMany(mappedBy = "engdu", cascade = CascadeType.ALL)
  private List<Article> articles = new ArrayList<>();

  public void validateOwner(Long userId) {
    if (!this.userId.equals(userId)) {
      throw new CustomException(ErrorCode.ENGDU_FORBIDDEN_ACCESS);
    }
  }
  private Engdu(Long userId, String title, String topic) {
    this.userId = userId;
    this.title = title;
    this.topic = topic;
  }

  public static Engdu of(Long userId, String title, String topic) {
    return new Engdu(userId, title, topic);
  }
}
