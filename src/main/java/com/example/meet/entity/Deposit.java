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
import java.util.List;

@Entity
@Data
@Table(name = "deposit")
public class Deposit {
    @Id
    @Column(name = "id")
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

    public void setIsDepositByName(String name) {
        DepositStatus depositStatus = Arrays.stream(DepositStatus.values())
                .filter(status -> status.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.DEPOSIT_STATUS_NOT_EXISTS));
        this.isDeposit = depositStatus;
    }

    public void setIsDepositFalse(){
        if(this.isDeposit == DepositStatus.COMPLETE){
            this.isDeposit = DepositStatus.NONE;
            this.setCount(null);
            this.setTotalCount(null);
        }
        else if(this.isDeposit == DepositStatus.PARTITION){
            this.isDeposit = DepositStatus.OVERDUE;
        }
    }

    public static List<DepositStatus> getIsDepositFalse(){
        return List.of(DepositStatus.NONE, DepositStatus.OVERDUE);
    }
}
