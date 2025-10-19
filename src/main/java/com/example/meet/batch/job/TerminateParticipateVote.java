package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.entity.Member;
import com.example.meet.entity.ParticipateVote;
import com.example.meet.entity.ParticipateVoteItem;
import com.example.meet.repository.BatchLogRepository;
import com.example.meet.repository.MeetRepository;
import com.example.meet.repository.ParticipateVoteRepository;
import java.util.ArrayList;
import java.util.List;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

public class TerminateParticipateVote extends CommonJob {
    private final ParticipateVoteRepository participateVoteRepository;
    private final MeetRepository meetRepository;

    @Autowired
    public TerminateParticipateVote(BatchLogRepository batchLogRepository, ParticipateVoteRepository participateVoteRepository,
            MeetRepository meetRepository) {
        super(batchLogRepository);
        this.participateVoteRepository = participateVoteRepository;
        this.meetRepository = meetRepository;
    }

    @Override
    protected String performJob(JobExecutionContext context) {
        List<ParticipateVote> participateVoteList = participateVoteRepository.findByEndDateToday();
        StringBuilder log = new StringBuilder();

        log.append("[");

        for(ParticipateVote participateVote : participateVoteList){
            participateVote.setTotalNum();
            Meet meet = participateVote.getMeet();

            meet.setParticipantsNum(participateVote.getTotalNum());

            ParticipateVoteItem item = participateVote.getParticipateVoteItems().stream()
                    .filter(ParticipateVoteItem::getIsParticipate)
                    .findFirst()
                    .orElse(null);

            if(item != null){
                // 방어적 복사
                List<Member> participateVoters = new ArrayList<>(item.getParticipateVoters());
                meet.setParticipants(participateVoters);
            }

            participateVoteRepository.save(participateVote);
            meetRepository.save(meet);

            log.append(participateVote.getMeet().getId());
            log.append(", ");
        }

        // 마지막 ", " 제거
        int index = log.lastIndexOf( ", ");

        if (index != -1 && index == log.length() - 2) {
            log.delete(index, log.length());
        }

        log.append("]");

        return log.toString();
    }
}
