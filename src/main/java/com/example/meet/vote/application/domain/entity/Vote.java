package com.example.meet.vote.application.domain.entity;

import com.example.meet.meet.application.domain.entity.Meet;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vote")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vote {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_num")
    private Long totalNum;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "place_result")
    private String placeResult;

    @Column(name = "date_result")
    private LocalDateTime dateResult;

    @Column(name = "active_yn")
    private Boolean activeYn;

    @OneToOne
    @JoinColumn(name = "meet_id", referencedColumnName = "id")
    private Meet meet;

    @OneToMany(mappedBy = "vote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<VoteItem> voteItems = new ArrayList<>();

    public void setPlaceResult() {
        if (this.voteItems.isEmpty()) {
            return;
        }

        List<VoteItem> itemsWithPlace = this.voteItems.stream()
                .filter(item -> item.getPlace() != null)
                .toList();

        if (itemsWithPlace.isEmpty()) {
            return;
        }

        int max = -1;
        VoteItem result = itemsWithPlace.get(0);
        Collections.sort(itemsWithPlace, Comparator.comparing(VoteItem::getId));

        for (VoteItem item : itemsWithPlace) {
            if (item.getVoters().size() > max) {
                max = item.getVoters().size();
                result = item;
            }
        }

        this.placeResult = result.getPlace();
    }

    public void setDateResult() {
        if (this.voteItems.isEmpty()) {
            return;
        }

        List<VoteItem> itemsWithDate = this.voteItems.stream()
                .filter(item -> item.getDate() != null)
                .toList();

        if (itemsWithDate.isEmpty()) {
            return;
        }

        int max = -1;
        LocalDateTime result = itemsWithDate.get(0).getDate();
        Collections.sort(itemsWithDate, Comparator.comparing(VoteItem::getDate));

        for (VoteItem item : itemsWithDate) {
            if (item.getVoters().size() > max) {
                max = item.getVoters().size();
                result = item.getDate();
            }
        }

        this.dateResult = result;
    }

    public void setTotalNum() {
        VoteItem item = this.voteItems.stream()
                .filter(VoteItem::getIsParticipate)
                .findFirst()
                .orElse(null);

        if (item != null) {
            this.totalNum = (long) item.getVoters().size();
        }
    }
}
