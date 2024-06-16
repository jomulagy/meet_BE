package com.example.meet.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
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
@Table(name = "place_vote")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceVote {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place_result")
    private String placeResult;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @OneToOne
    @JoinColumn(name = "meet_id", referencedColumnName = "id")
    private Meet meet;

    @OneToMany(mappedBy = "placeVote", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PlaceVoteItem> placeVoteItems = new ArrayList<>();

    public void setPlaceResult(){
        int max = -1;
        String result = null;
        Collections.sort(this.placeVoteItems, Comparator.comparing(PlaceVoteItem::getId));

        for(PlaceVoteItem item : this.placeVoteItems){
            if(item.getPlaceVoters().size() > max){
                max = item.getPlaceVoters().size();
                result = item.getPlace();
            }
        }

        this.placeResult = result;
    }
}
