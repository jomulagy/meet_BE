package com.example.meet.api.participate.application.domain.entity;

import com.example.meet.api.member.application.domain.entity.Member;
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

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "participate_vote_item")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipateVoteItem {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "isParticipate", nullable = false)
    private Boolean isParticipate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participateVote_id")
    private ParticipateVote participateVote;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "participate_vote_item_member",
            joinColumns = @JoinColumn(name = "participateVoteItem_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    @Builder.Default
    private List<Member> participateVoters = new ArrayList<>();

    public void vote(List<Member> members){
        participateVoters.clear();
        participateVoters = members;
    }
}
