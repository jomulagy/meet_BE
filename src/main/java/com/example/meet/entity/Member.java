package com.example.meet.entity;

import com.example.meet.infrastructure.enumulation.MemberPrevillege;
import com.example.meet.participate.application.domain.entity.ParticipateVote;
import com.example.meet.participate.application.domain.entity.ParticipateVoteItem;
import com.example.meet.post.application.domain.entity.Post;
import com.example.meet.vote.application.domain.entity.VoteItem;
import jakarta.persistence.*;

import java.util.List;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "member")
public class Member {
    @Id
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "previllege",nullable = false)
    private MemberPrevillege previllege = MemberPrevillege.denied;

    @Column(name = "deposit",nullable = false)
    private Boolean deposit = false;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<VoteItem> scheduleVoteItemList = new ArrayList<>();

    @ManyToMany(mappedBy = "voters")
    private List<VoteItem> scheduleVoters = new ArrayList<>();

    @ManyToMany(mappedBy = "participateVoters")
    private List<ParticipateVoteItem> participateVoters = new ArrayList<>();

    public void updatePrevillege(MemberPrevillege previllege){
        this.previllege = previllege;
    }

    public void updateUUID(String uuid) {
        this.uuid = uuid;
    }

    public void setDeposit(Boolean deposit) {
        this.deposit = deposit;
    }

    public void setPrevillege(MemberPrevillege previllege) {
        this.previllege = previllege;
    }
}
