package com.example.meet.entity;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "schedule_vote_item")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleVoteItem {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "editable", nullable = false)
    private Boolean editable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scheduleVote_id")
    private ScheduleVote scheduleVote;

    @ManyToMany
    @JoinTable(
            name = "schedule_vote_item_member",
            joinColumns = @JoinColumn(name = "scheduleVoteItem_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @Builder.Default
    private List<Member> scheduleVoters = new ArrayList<>();
}
