package com.example.meet.entity;

import com.example.meet.common.dto.request.EditMeetRequestDto;
import com.example.meet.common.enumulation.MeetType;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

    @Column(name = "type", nullable = false)
    private MeetType type;

    @Column(name = "content")
    private String content;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "place")
    private String place;

    @Column(name = "participantsNum")
    private Long participantsNum;

    @OneToOne(mappedBy = "meet", cascade = CascadeType.ALL, orphanRemoval = true)
    private ScheduleVote scheduleVote;

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

    void setDate(){
        this.date = this.scheduleVote.getDateResult();
    }

    void setPlace(){
        this.place = this.placeVote.getPlaceResult();
    }
    void setParticipateNum(){
        this.participantsNum = this.participateVote.getTotalNum();
    }

    public void setParticipants(List<Member> participants) {
        this.participants = participants;
    }

    public void setScheduleVote(ScheduleVote scheduleVote){
        this.scheduleVote = scheduleVote;
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
        this.place = inDto.getPlace();
    }

    public void setDateResult(LocalDateTime dateResult) {
        this.date = dateResult;
    }

    public void setPlaceResult(String place) {
        this.place = place;
    }

    public void setParticipantsNum(long totalNum) {
        this.participantsNum = totalNum;
    }
}
