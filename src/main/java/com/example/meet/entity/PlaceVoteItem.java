package com.example.meet.entity;

import com.example.meet.common.enumulation.PlaceType;
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
import java.math.BigDecimal;
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

    @Column(name = "place", nullable = false)
    private String place;
    @Column(name = "x_pos", nullable = false)
    private BigDecimal xPos;
    @Column(name = "y_pos", nullable = false)
    private BigDecimal yPos;

    @Column(name = "type", nullable = false)
    private PlaceType type;

    @Column(name = "editable", nullable = false)
    private Boolean editable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placeVote_id")
    private PlaceVote placeVote;

    @ManyToMany
    @JoinTable(
            name = "place_vote_item_member",
            joinColumns = @JoinColumn(name = "placeVoteItem_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @Builder.Default
    private List<Member> placeVoters = new ArrayList<>();
}
