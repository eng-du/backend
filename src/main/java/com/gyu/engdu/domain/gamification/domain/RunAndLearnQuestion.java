package com.gyu.engdu.domain.gamification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunAndLearnQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "run_and_learn_question_id")
    private Long id;

    @Column(nullable = false)
    private String question;

    @Column(nullable = false)
    private Integer answer;

    @Column(nullable = false)
    private String choice1;

    @Column(nullable = false)
    private String choice2;

    @Column(nullable = false)
    private String choice3;

    private String explanation;

}
