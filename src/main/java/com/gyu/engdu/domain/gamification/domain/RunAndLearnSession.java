package com.gyu.engdu.domain.gamification.domain;

import com.gyu.engdu.domain.BaseEntity;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnSessionAlreadyStartedException;
import com.gyu.engdu.domain.gamification.exception.RunAndLearnSessionForbiddenAccessException;
import com.gyu.engdu.domain.user.domain.User;
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

    @Builder
    private RunAndLearnSession(User user, int seed, int score) {
        this.user = user;
        this.seed = seed;
        this.score = score;
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
            throw new RunAndLearnSessionForbiddenAccessException(userId, this.id, this.user.getId());
        }
    }

    public void start(LocalDateTime startTime) {
        if (this.startedAt != null) {
            throw new RunAndLearnSessionAlreadyStartedException(this.id);
        }
        this.startedAt = startTime;
    }

}
