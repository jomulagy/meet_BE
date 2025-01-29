package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.entity.Member;
import com.example.meet.entity.ParticipateVote;
import com.example.meet.entity.ParticipateVoteItem;
import com.example.meet.repository.BatchLogRepository;
import com.example.meet.repository.ParticipateVoteRepository;
import java.util.ArrayList;
import java.util.List;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

public class TerminateParticipateVote extends CommonJob {
    private final ParticipateVoteRepository participateVoteRepository;

    @Autowired
    public TerminateParticipateVote(BatchLogRepository batchLogRepository, ParticipateVoteRepository participateVoteRepository) {
        super(batchLogRepository);
        this.participateVoteRepository = participateVoteRepository;
    }

    @Override
    protected void performJob(JobExecutionContext context) {
        List<ParticipateVote> participateVoteList = participateVoteRepository.findByEndDateToday();

        for(ParticipateVote participateVote : participateVoteList){
            participateVote.setTotalNum();
            participateVote.getMeet().setParticipantsNum(participateVote.getTotalNum());

            ParticipateVoteItem item = participateVote.getParticipateVoteItems().stream()
                    .filter(ParticipateVoteItem::getIsParticipate)
                    .findFirst()
                    .orElse(null);

            if(item != null){
                // 방어적 복사
                List<Member> participateVoters = new ArrayList<>(item.getParticipateVoters());
                participateVote.getMeet().setParticipants(participateVoters);
            }

            taskList.add(participateVote.getMeet().getId().toString());
        }
    }
}
