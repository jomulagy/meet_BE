package com.example.meet.api.post.application.domain.entity;

import com.example.meet.api.participate.application.domain.entity.ParticipateVote;
import com.example.meet.api.vote.application.domain.entity.Vote;
import com.example.meet.infrastructure.config.jpaAudit.domain.entity.BaseAuditEntity;
import com.example.meet.infrastructure.enumulation.PostType;
import com.example.meet.infrastructure.enumulation.VoteStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseAuditEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "type")
    private PostType type;

    @Column(name = "participantsNum")
    private Long participantsNum;

    @Column(name = "status")
    private VoteStatus status;

    @Column(name = "meet_date")
    private LocalDate meetDate;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> voteList = new ArrayList<>();

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private ParticipateVote participateVote;

    public void addVote(Vote vote) {
        this.voteList.add(vote);
        vote.setPost(this);
    }
}
