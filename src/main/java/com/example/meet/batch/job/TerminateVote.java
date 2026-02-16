package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
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
        try {
            Long voteId = context.getJobDetail().getJobDataMap().getLong("voteId");
            Vote vote = getVotePort.get(voteId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.VOTE_NOT_EXISTS));

            process(vote);

            return vote.getTitle();

        } catch (Exception e) {
            super.insertBatch("Fail to terminate vote", "FAILURE", e.getMessage());
            throw e;
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void process(Vote vote) {
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

        try {
            sendMessageUseCase.sendVoteTerminated(vote.getTitle(), vote.getId().toString());

        } catch (Exception e) {
            super.insertBatch("Fail to send participate vote message :: " + vote.getId(), "FAILURE", e.getMessage());
            throw e;
        }
    }
}
