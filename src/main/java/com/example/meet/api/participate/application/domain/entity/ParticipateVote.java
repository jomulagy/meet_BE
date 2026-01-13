package com.example.meet.api.participate.application.domain.entity;

import com.example.meet.api.post.application.domain.entity.Post;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.*;

@Entity
@Table(name = "participate_vote")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipateVote {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_num")
    @Setter
    private long totalNum;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "isActive")
    private boolean isActive;

    @Setter
    @OneToOne
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    @OneToMany(mappedBy = "participateVote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<ParticipateVoteItem> participateVoteItems = new ArrayList<>();
}
