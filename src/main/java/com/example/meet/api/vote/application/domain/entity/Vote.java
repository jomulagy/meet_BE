package com.example.meet.api.vote.application.domain.entity;

import com.example.meet.infrastructure.enumulation.VoteType;
import com.example.meet.api.post.application.domain.entity.Post;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.meet.infrastructure.enumulation.VoteType.TEXT;

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

    @Column(name = "title")
    private String title;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "active_yn")
    private Boolean activeYn;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    @Builder.Default
    private VoteType type = TEXT;

    @Column(name = "isDuplicate")
    @Builder.Default
    private boolean isDuplicate = false;

    @Column(name = "result")
    private String result;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<VoteItem> voteItems = new ArrayList<>();
}
