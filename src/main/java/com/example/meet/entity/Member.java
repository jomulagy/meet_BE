package com.example.meet.entity;

import com.example.meet.common.enumulation.DepositStatus;
import com.example.meet.common.enumulation.ErrorCode;
import com.example.meet.common.enumulation.MemberPrevillege;
import com.example.meet.common.exception.BusinessException;
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
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.beans.BulkBeanException;

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

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Privilege privilege;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Deposit deposit;

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

    public void updateUUID(String uuid) {
        this.uuid = uuid;
    }

    public void setDeposit(Deposit deposit) {
        this.deposit = deposit;
    }

    public Boolean hasPrivilege(){
        Boolean result = Boolean.FALSE;

        if(this.getPrivilege() != null){
            result = this.getPrivilege().getStatus() != MemberPrevillege.denied;
        }

        return result;
    }

    public Boolean isAdmin(){
        Boolean result = Boolean.FALSE;

        if(this.getPrivilege() != null){
            result = this.getPrivilege().getStatus() == MemberPrevillege.admin;
        }

        return result;
    }

    public void setPrivilege(MemberPrevillege memberPrevillege) {
        if(this.privilege == null){
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        this.privilege.setStatus(memberPrevillege);
    }

    // 미납이거나, 연체 상태일때 FALSE
    // 완납이거나, 연체 상태 아닐때 TRUE
    public Boolean hasDeposit(){
        return this.deposit.getIsDeposit().getCode() == 1 || this.deposit.getIsDeposit().getCode() == 4;
    }

    public void setIsDepositByName(String name) {
        if(this.deposit != null){
            this.deposit.setIsDepositByName(name);
        }
    }

    public void setIsDepositFalse(){
        if(this.deposit != null){
            this.deposit.setIsDepositFalse();
        }
    }
}
