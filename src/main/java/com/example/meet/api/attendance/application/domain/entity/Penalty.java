package com.example.meet.api.attendance.application.domain.entity;

import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.api.post.application.domain.entity.Post;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "penalty")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Penalty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(name = "consecutive_absences", nullable = false)
    private Integer consecutiveAbsences;

    @Column(name = "penalty_paid", nullable = false)
    @Builder.Default
    private Boolean penaltyPaid = false;

    @Column(name = "payment_deadline")
    private LocalDate paymentDeadline;

    @Column(name = "batch_scheduled")
    private Boolean batchScheduled;

    @Column(name = "batch_scheduled_date")
    private LocalDate batchScheduledDate;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public void markPenaltyPaid() {
        this.penaltyPaid = true;
    }

    public void cancelBatch() {
        this.batchScheduled = false;
        this.batchScheduledDate = null;
    }
}
