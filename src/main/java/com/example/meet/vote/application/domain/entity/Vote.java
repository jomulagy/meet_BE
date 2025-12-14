package com.example.meet.vote.application.domain.entity;

import com.example.meet.meet.application.domain.entity.Meet;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.meet.vote.application.domain.entity.VoteType.DATE;

@Entity
@Table(name = "vote")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "active_yn")
    private Boolean activeYn;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @Builder.Default
    private VoteType type = DATE;

    @Column(name = "result")
    private String result;

    @Column(name = "display_order")
    private Integer displayOrder;

    @OneToOne
    @JoinColumn(name = "meet_id", referencedColumnName = "id")
    private Meet meet;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<VoteItem> voteItems = new ArrayList<>();
}
