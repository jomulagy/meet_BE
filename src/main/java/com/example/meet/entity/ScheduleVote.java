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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "schedule_vote")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleVote {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_result")
    private LocalDateTime dateResult;

    @Column(name = "end_date")
    private LocalDate endDate;

    @OneToOne
    @JoinColumn(name = "meet_id", referencedColumnName = "id")
    private Meet meet;

    @OneToMany(mappedBy = "scheduleVote", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ScheduleVoteItem> scheduleVoteItems = new ArrayList<>();

    public void setDateResult(){
        int max = -1;
        LocalDateTime result = this.scheduleVoteItems.get(0).getDate();
        Collections.sort(this.scheduleVoteItems, Comparator.comparing(ScheduleVoteItem::getDate));

        for(ScheduleVoteItem item : this.scheduleVoteItems){
            if(item.getScheduleVoters().size() > max){
                max = item.getScheduleVoters().size();
                result = item.getDate();
            }
        }
        this.dateResult = result;
    }
}
