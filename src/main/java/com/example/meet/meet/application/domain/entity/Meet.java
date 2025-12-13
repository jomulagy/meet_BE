package com.example.meet.meet.application.domain.entity;

import com.example.meet.infrastructure.dto.request.EditMeetRequestDto;
import com.example.meet.entity.Member;
import com.example.meet.entity.ParticipateVote;
import com.example.meet.entity.PlaceVote;
import com.example.meet.vote.application.domain.entity.Vote;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
public class Meet {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content")
    private String content;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "place")
    private String place;

    @Column(name = "participantsNum")
    private Long participantsNum;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @OneToOne(mappedBy = "meet", cascade = CascadeType.ALL, orphanRemoval = true)
    private Vote scheduleVote;

    @OneToOne(mappedBy = "meet", cascade = CascadeType.ALL, orphanRemoval = true)
    private PlaceVote placeVote;

    @OneToOne(mappedBy = "meet", cascade = CascadeType.ALL, orphanRemoval = true)
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

    public void setPlace(){
        this.place = this.placeVote.getPlaceResult();
    }

    void setPlace(EditMeetRequestDto inDto){
        this.place = inDto.getPlace();
    }
    void setParticipateNum(){
        this.participantsNum = this.participateVote.getTotalNum();
    }

    public void setParticipants(List<Member> participants) {
        this.participants = participants;
    }

    public void setPlaceVote(PlaceVote placeVote) {
        this.placeVote = placeVote;
    }

    public void setParticipateVote(ParticipateVote participateVote) {
        this.participateVote = participateVote;
    }

    public void update(EditMeetRequestDto inDto) {
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

    public void setDateResult(LocalDateTime dateResult) {
        this.date = dateResult;
    }

    public void setParticipantsNum(long totalNum) {
        this.participantsNum = totalNum;
    }
}
