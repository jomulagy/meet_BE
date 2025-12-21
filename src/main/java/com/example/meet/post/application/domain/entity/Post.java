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
@Table(name = "meet")
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

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Vote> voteList = new ArrayList<>();

    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private ParticipateVote participateVote;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author", nullable = false)
    private Member author;

    @ManyToMany
    @JoinTable(
            name = "participants",
            joinColumns = @JoinColumn(name = "meet_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @Builder.Default
    private List<Member> participants = new ArrayList<>();


    void setPlace(UpdatePostRequestDto inDto){
        this.place = inDto.getPlace();
    }

    public void setParticipants(List<Member> participants) {
        this.participants = participants;
    }


    public void setParticipateVote(ParticipateVote participateVote) {
        this.participateVote = participateVote;
    }

    public void update(UpdatePostRequestDto inDto) {
        this.title = inDto.getTitle();
        this.content = inDto.getContent();
        if (inDto.getDate() == null) {
            this.date = null;
        } else {
            LocalTime time = (inDto.getTime() != null) ? inDto.getTime() : LocalTime.of(0, 0);
            this.date = LocalDateTime.of(inDto.getDate(), time);
        }

        if(inDto.getPlace() != null){
            this.setPlace(inDto);
        }
    }

    public void setParticipantsNum(long totalNum) {
        this.participantsNum = totalNum;
    }
}
