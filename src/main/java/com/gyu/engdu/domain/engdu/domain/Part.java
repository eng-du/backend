package com.gyu.engdu.domain.engdu.domain;

import java.time.LocalDateTime;

import com.gyu.engdu.domain.BaseEntity;
import com.gyu.engdu.domain.engdu.domain.enums.PartStatus;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PartStatus status = PartStatus.PENDING;

    private LocalDateTime retryAllowedAt;

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
        this.status = PartStatus.PENDING;
    }

    public void changeStatus(PartStatus status) {
        this.status = status;
    }

    /**
     * 상태를 CREATING으로 변경하고, 재시도 허용 시각을 기록합니다.
     * 현재 시각이 retryAllowedAt을 초과하면 이전 워커가 크래시된 것으로 간주합니다.
     *
     * @param retryAllowedAt 이 시각 이후에 재시도가 허용됩니다. (= now + visibility timeout)
     */
    public void startCreation(LocalDateTime retryAllowedAt) {
        this.status = PartStatus.CREATING;
        this.retryAllowedAt = retryAllowedAt;
    }

    // 메시지 발행 가능 여부를 반환합니다.
    public boolean isPublishable() {
        return status == PartStatus.PENDING || status == PartStatus.FAILED;
    }

    // DONE 상태로, 이미 완료된 파트입니다.
    public boolean isDone() {
        return status == PartStatus.DONE;
    }

    /**
     * 현재 다른 워커가 이 파트를 생성 중인지 확인합니다.
     * CREATING 상태이면서 아직 재시도 허용 시각이 되지 않았을 때 true를 반환합니다.
     */
    public boolean isProcessing() {
        return status == PartStatus.CREATING
                && retryAllowedAt != null
                && LocalDateTime.now().isBefore(retryAllowedAt);
    }

    public void validateOwner(Long userId) {
        engdu.validateOwner(userId);
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
