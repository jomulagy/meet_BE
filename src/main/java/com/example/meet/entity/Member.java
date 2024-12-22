package com.example.meet.entity;

import com.example.meet.common.enumulation.MemberPrevillege;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
    private List<ScheduleVoteItem> scheduleVoteItemList = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PlaceVoteItem> placeVoteItemList = new ArrayList<>();

    @ManyToMany(mappedBy = "scheduleVoters")
    private List<ScheduleVoteItem> scheduleVoters = new ArrayList<>();

    @ManyToMany(mappedBy = "placeVoters")
    private List<PlaceVoteItem> placeVoteItems = new ArrayList<>();

    @ManyToMany(mappedBy = "participateVoters")
    private List<ParticipateVoteItem> participateVoters = new ArrayList<>();

    @ManyToMany(mappedBy = "participants")
    private List<Meet> meets = new ArrayList<>();

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
