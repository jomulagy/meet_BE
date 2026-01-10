package com.example.meet.post.application.domain.entity;

import com.example.meet.infrastructure.enumulation.VoteStatus;
import com.example.meet.entity.Member;
import com.example.meet.participate.application.domain.entity.ParticipateVote;
import com.example.meet.infrastructure.enumulation.PostType;
import com.example.meet.vote.application.domain.entity.Vote;
import jakarta.persistence.*;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "post")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {
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

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> voteList = new ArrayList<>();

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private ParticipateVote participateVote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author", nullable = false)
    private Member author;

    public void addVote(Vote vote) {
        this.voteList.add(vote);
        vote.setPost(this);
    }
}
