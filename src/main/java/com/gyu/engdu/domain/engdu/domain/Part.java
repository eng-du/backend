package com.gyu.engdu.domain.engdu.domain;

import com.gyu.engdu.domain.BaseEntity;
import com.gyu.engdu.domain.engdu.domain.enums.PartType;
import com.gyu.engdu.domain.engdu.exception.QuestionForbiddenAccessException;
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
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Part extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "part_type", nullable = false)
    private PartType partType;

    private boolean isAllSolved = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "engdu_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Engdu engdu;

    @OneToMany(mappedBy = "part", cascade = CascadeType.ALL)
    private List<Question> questions = new ArrayList<>();

    @OneToOne(mappedBy = "part", cascade = CascadeType.ALL, orphanRemoval = true)
    private Article article;

    @Builder
    private Part(PartType partType) {
        this.partType = partType;
    }

    public static Part of(PartType partType, Engdu engdu) {
        Part part = Part.builder()
                .partType(partType)
                .build();
        part.setEngdu(engdu);
        return part;
    }

    private void setEngdu(Engdu engdu) {
        this.engdu = engdu;
        engdu.getParts().add(this);
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    /**
     * 파트 내 문제를 풀이한다.
     * 해당 파트의 모든 문제를 다 풀었으면 isAllSolved를 true로 변경하고 true를 반환한다.
     */
    public boolean solveQuestion(Long questionId, Byte userAnswer) {
        Question question = findQuestion(questionId);
        boolean isAnswered = question.solve(userAnswer);

        if (isAnswered) {
            checkAndMarkAllSolved();
        }

        return isAnswered;
    }

    private Question findQuestion(Long questionId) {
        return questions.stream()
                .filter(q -> q.getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new QuestionForbiddenAccessException(engdu.getId(), questionId));
    }

    private void checkAndMarkAllSolved() {
        boolean allSolved = questions.stream()
                .allMatch(Question::isCorrected);

        if (allSolved) {
            this.isAllSolved = Boolean.TRUE;
        }
    }
}
