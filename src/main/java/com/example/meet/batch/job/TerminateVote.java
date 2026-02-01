package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.infrastructure.repository.BatchLogRepository;
import com.example.meet.infrastructure.utils.MessageManager;
import com.example.meet.api.message.application.port.in.SendMessageUseCase;
import com.example.meet.api.post.application.port.out.UpdatePostPort;
import com.example.meet.api.vote.application.domain.entity.Vote;
import com.example.meet.api.vote.application.domain.entity.VoteItem;
import com.example.meet.api.vote.application.port.out.GetVotePort;
import com.example.meet.api.vote.application.port.out.UpdateVotePort;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public class TerminateVote extends CommonJob {
    private final GetVotePort getVotePort;
    private final UpdateVotePort updateVotePort;
    private final SendMessageUseCase sendMessageUseCase;

    public TerminateVote(BatchLogRepository batchLogRepository,
                         GetVotePort getVotePort, UpdatePostPort updatePostPort, UpdateVotePort updateVotePort, MessageManager messageManager, SendMessageUseCase sendMessageUseCase) {
        super(batchLogRepository);
        this.sendMessageUseCase = sendMessageUseCase;
        this.getVotePort = getVotePort;
        this.updateVotePort = updateVotePort;
    }

    @Override
    @Transactional
    protected String performJob(JobExecutionContext context) {
        LocalDateTime currentDate = LocalDateTime.now();
        List<Vote> voteList = getVotePort.getListByEndDate(currentDate);
        StringBuilder log = new StringBuilder();

        log.append("[");

        for(Vote vote : voteList){
            try {
                process(vote, log);
                
            } catch (Exception e) {
                continue;
            }
        }

        // 마지막 ", " 제거
        int index = log.lastIndexOf( ", ");

        if (index != -1 && index == log.length() - 2) {
            log.delete(index, log.length());
        }

        log.append("]");

        return log.toString();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void process(Vote vote, StringBuilder log) {
        try {
            VoteItem result;
            List<VoteItem> voteItemList = vote.getVoteItems();

            if(voteItemList.isEmpty()){
                return;
            }

            int max = -1;
            result = null;

            for(VoteItem item : voteItemList){
                if(item.getVoters().size() > max){
                    max = item.getVoters().size();

                    result = item;
                }
            }

            updateVotePort.updateResult(vote.getId(), result);

        } catch (Exception e) {
            super.insertBatch("Fail to terminate vote :: " + vote.getId(), "FAILURE", e.getMessage());
            throw e;
        }

        try {
            sendMessageUseCase.sendParticipate(vote.getTitle(), vote.getId().toString());

        } catch (Exception e) {
            super.insertBatch("Fail to send participate vote message :: " + vote.getId(), "FAILURE", e.getMessage());
            throw e;
        }

        log.append(vote.getTitle());
        log.append(", ");
    }
}
