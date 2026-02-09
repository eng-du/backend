package com.gyu.engdu.domain.learning.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "unique_phrasalverb_en", columnNames = { "en" })
})
public class PhrasalVerb {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "phrasal_verb_id")
  private Long id;

  private String en;

  private String kor;

  private String exampleSentenceEn;

  private String exampleSentenceKor;

}
