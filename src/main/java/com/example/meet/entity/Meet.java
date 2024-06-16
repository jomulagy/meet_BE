package com.example.meet.entity;

import com.example.meet.common.enumulation.MeetType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
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

    @ManyToMany
    @JoinTable(
            name = "participants",
            joinColumns = @JoinColumn(name = "meet_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private List<Member> participants = new ArrayList<>();

    void setDate(){
        this.date = this.scheduleVote.getDateResult().atTime(19, 0);
    }

    void setPlace(){
        this.place = this.placeVote.getPlaceResult();
    }
    void setParticipateNum(){
        this.participantsNum = this.participateVote.getTotalNum();
    }
}
