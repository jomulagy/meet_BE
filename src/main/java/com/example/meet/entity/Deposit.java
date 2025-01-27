package com.example.meet.entity;

import com.example.meet.common.enumulation.DepositStatus;
import com.example.meet.common.enumulation.ErrorCode;
import com.example.meet.common.exception.BusinessException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Arrays;
import lombok.Data;

@Entity
@Data
@Table(name = "deposit")
public class Deposit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "count")
    private Integer count;

    @Column(name = "total_count")
    private Integer totalCount;

    @Column(name = "is_deposit")
    private DepositStatus isDeposit;

    @OneToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id")
    private Member member;

    // 미납이거나, 연체 상태일때 FALSE
    // 완납이거나, 연체 상태 아닐때 TRUE
    public Boolean getIsDeposit(){
        return this.isDeposit.getCode() == 1 || this.isDeposit.getCode() == 4;
    }

    public DepositStatus setIsDepositByName(String name) {
        return Arrays.stream(DepositStatus.values())
                .filter(status -> status.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.DEPOSIT_STATUS_NOT_EXISTS));
    }

    public void setIsDepositFalse(){
        this.setCount(null);
        this.setIsDeposit(DepositStatus.NONE);
        this.setTotalCount(null);
    }
}
