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

    @OneToOne
    @JoinColumn(name = "meet_id", referencedColumnName = "id")
    private Meet meet;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<VoteItem> voteItems = new ArrayList<>();
}
