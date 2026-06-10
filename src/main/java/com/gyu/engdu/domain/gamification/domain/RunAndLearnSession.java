package com.gyu.engdu.domain.gamification.domain;

import com.gyu.engdu.domain.BaseEntity;
import com.gyu.engdu.domain.gamification.domain.enums.RunAndLearnSessionStatus;
import com.gyu.engdu.domain.gamification.exception.InvalidRunAndLearnStatusException;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnSessionAlreadyStartedException;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnSessionForbiddenAccessException;
import com.gyu.engdu.domain.user.domain.User;
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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RunAndLearnSession extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "run_and_learn_session_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    private int seed;

    private int score;

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RunAndLearnSessionStatus status;

    @Builder
    private RunAndLearnSession(User user, int seed, int score) {
        this.user = user;
        this.seed = seed;
        this.score = score;
        this.status = RunAndLearnSessionStatus.INIT;
    }

    public static RunAndLearnSession of(User user, int seed) {
        return RunAndLearnSession.builder()
                .user(user)
                .seed(seed)
                .score(0)
                .build();
    }

    public void validateOwner(Long userId) {
        if (!this.user.getId().equals(userId)) {
            throw new RunAndLearnSessionForbiddenAccessException(userId, this.id,
                    this.user.getId());
        }
    }

    public void start(LocalDateTime startTime) {
        if (this.status != RunAndLearnSessionStatus.INIT) {
            throw new RunAndLearnSessionAlreadyStartedException(this.id);
        }
        this.startedAt = startTime;
        this.status = RunAndLearnSessionStatus.PROGRESS;
    }

    public void end(int finalScore, LocalDateTime endTime) {
        if (this.status == RunAndLearnSessionStatus.ENDED
                || this.status == RunAndLearnSessionStatus.INIT) {
            throw new InvalidRunAndLearnStatusException(this.status);
        }
        this.score = finalScore;
        this.endedAt = endTime;
        this.status = RunAndLearnSessionStatus.ENDED;
    }

    public void validateEndedStatus() {
        if (this.status != RunAndLearnSessionStatus.ENDED) {
            throw new InvalidRunAndLearnStatusException(this.status);
        }
    }
}
