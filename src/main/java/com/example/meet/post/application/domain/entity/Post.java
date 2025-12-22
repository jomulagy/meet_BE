package com.example.meet.post.application.domain.entity;

import com.example.meet.post.adapter.in.dto.in.UpdatePostRequestDto;
import com.example.meet.entity.Member;
import com.example.meet.entity.ParticipateVote;
import com.example.meet.infrastructure.enumulation.MeetType;
import com.example.meet.vote.application.domain.entity.Vote;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private MeetType type;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "place")
    private String place;

    @Column(name = "participantsNum")
    private Long participantsNum;

    @Builder.Default
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> voteList = new ArrayList<>();

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private ParticipateVote participateVote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author", nullable = false)
    private Member author;

    @ManyToMany
    @JoinTable(
            name = "participants",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @Builder.Default
    private List<Member> participants = new ArrayList<>();

    public void setParticipants(List<Member> participants) {
        this.participants = participants;
    }

    public void setParticipateVote(ParticipateVote participateVote) {
        this.participateVote = participateVote;
        if (participateVote != null) {
            participateVote.setPost(this);
        }
    }

    public void setParticipantsNum(long totalNum) {
        this.participantsNum = totalNum;
    }

    public void addVote(Vote vote) {
        this.voteList.add(vote);
        vote.setPost(this);
    }
}
