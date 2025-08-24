package com.example.meet.entity;

import com.example.meet.meet.domain.entity.Meet;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "participate_vote")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParticipateVote {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "total_num")
    private long totalNum;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @OneToOne
    @JoinColumn(name = "meet_id", referencedColumnName = "id")
    private Meet meet;

    @OneToMany(mappedBy = "participateVote", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<ParticipateVoteItem> participateVoteItems = new ArrayList<>();

    public void setTotalNum(){
        ParticipateVoteItem item = this.participateVoteItems.stream()
                .filter(ParticipateVoteItem::getIsParticipate)
                .findFirst()
                .orElse(null);

        if(item != null){
            this.totalNum = item.getParticipateVoters().size();
        }
    }
}
