package com.example.meet.entity;

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
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "place_vote_item")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceVoteItem {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "place")
    private String place;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placeVote_id")
    private PlaceVote placeVote;

    @ManyToMany
    @JoinTable(
            name = "place_vote_item_member",
            joinColumns = @JoinColumn(name = "placeVoteItem_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private List<Member> placeVoters = new ArrayList<>();

    public void vote(List<Member> members){
        placeVoters.clear();
        placeVoters = members;
    }
}
