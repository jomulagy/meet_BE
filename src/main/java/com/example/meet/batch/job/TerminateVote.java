package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.entity.PlaceVote;
import com.example.meet.infrastructure.dto.TemplateArgs;
import com.example.meet.infrastructure.enumulation.Message;
import com.example.meet.infrastructure.enumulation.VoteType;
import com.example.meet.infrastructure.repository.BatchLogRepository;
import com.example.meet.infrastructure.repository.MeetRepository;
import com.example.meet.infrastructure.repository.PlaceVoteRepository;
import com.example.meet.infrastructure.utils.MessageManager;
import com.example.meet.post.application.port.out.GetPostPort;
import com.example.meet.post.application.port.out.UpdatePostPort;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.domain.entity.VoteItem;
import com.example.meet.vote.application.port.out.GetVotePort;
import com.example.meet.vote.application.port.out.UpdateVotePort;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

public class TerminateVote extends CommonJob {
    private final GetVotePort getVotePort;
    private final UpdatePostPort updatePostPort;
    private final UpdateVotePort updateVotePort;
    private final MessageManager messageManager;

    public TerminateVote(BatchLogRepository batchLogRepository,
                         GetVotePort getVotePort, UpdatePostPort updatePostPort, UpdateVotePort updateVotePort, MessageManager messageManager) {
        super(batchLogRepository);;
        this.getVotePort = getVotePort;
        this.updatePostPort = updatePostPort;
        this.updateVotePort = updateVotePort;
        this.messageManager = messageManager;
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
            String contentResult;
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

            if(VoteType.DATE.equals(vote.getType())) {
                contentResult = result.getDateTime().toString();
                updatePostPort.updateDate(vote.getPost().getId(), result.getDateTime());
            } else if(VoteType.PLACE.equals(vote.getType())) {
                contentResult = result.getContent();
                updatePostPort.updatePlace(vote.getPost().getId(), result.getContent());
            } else {
                contentResult = result.getContent();
            }

            updateVotePort.updateResult(vote.getId(), contentResult);

        } catch (Exception e) {
            super.insertBatch("Fail to terminate ScheduleVote :: " + vote.getId(), "FAILURE", e.getMessage());
            throw e;
        }

        try {
            TemplateArgs templateArgs = TemplateArgs.builder()
                    .title(vote.getTitle())
                    .scheduleType(null)
                    .but(vote.getId().toString())
                    .build();
            Message.VOTE.setTemplateArgs(templateArgs);
            messageManager.sendAll(Message.VOTE).block();
            messageManager.sendMe(Message.VOTE).block();
        } catch (Exception e) {
            super.insertBatch("Fail to send participate vote message :: " + vote.getId(), "FAILURE", e.getMessage());
            throw e;
        }

        log.append(vote.getTitle());
        log.append(", ");
    }
}
