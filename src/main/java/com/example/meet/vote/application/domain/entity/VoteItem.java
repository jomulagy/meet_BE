package com.example.meet.vote.application.domain.entity;

import com.example.meet.entity.Member;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vote_item")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VoteItem {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_participate")
    private Boolean isParticipate;

    @Column(name = "place")
    private String place;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "editable", nullable = false)
    private Boolean editable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    private Member author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id")
    private Vote vote;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "vote_item_member",
            joinColumns = @JoinColumn(name = "vote_item_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @Builder.Default
    private List<Member> voters = new ArrayList<>();
}
