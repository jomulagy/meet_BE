package com.example.meet.batch.job;

import com.example.meet.api.participate.adapter.in.dto.in.TerminateParticipateVoteRequestDto;
import com.example.meet.api.participate.application.domain.entity.ParticipateVote;
import com.example.meet.api.participate.application.port.in.UpdateParticipateVoteUseCase;
import com.example.meet.api.participate.application.port.out.GetParticipateVotePort;
import com.example.meet.api.vote.adapter.in.dto.in.TerminateVoteRequestDto;
import com.example.meet.api.vote.application.domain.entity.Vote;
import com.example.meet.api.vote.application.domain.entity.VoteItem;
import com.example.meet.batch.CommonJob;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.repository.BatchLogRepository;
import com.example.meet.infrastructure.repository.MeetRepository;
import com.example.meet.infrastructure.repository.ParticipateVoteRepository;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class TerminateParticipateVote extends CommonJob {
    private final GetParticipateVotePort getParticipateVotePort;
    private final UpdateParticipateVoteUseCase updateParticipateVoteUseCase;

    @Autowired
    public TerminateParticipateVote(
            BatchLogRepository batchLogRepository,
            GetParticipateVotePort getParticipateVotePort,
            UpdateParticipateVoteUseCase updateParticipateVoteUseCase
    ) {
        super(batchLogRepository);
        this.getParticipateVotePort = getParticipateVotePort;
        this.updateParticipateVoteUseCase = updateParticipateVoteUseCase;
    }

    @Override
    protected String performJob(JobExecutionContext context) {
        StringBuilder log = new StringBuilder();

        try {
            Long voteId = context.getJobDetail().getJobDataMap().getLong("voteId");
            ParticipateVote vote = getParticipateVotePort.get(voteId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.VOTE_NOT_EXISTS));

            process(vote);

            return vote.getPost().getTitle();
        } catch (Exception e) {
            super.insertBatch("Fail to terminate participate vote", "FAILURE", e.getMessage());
            throw e;
        }
    }

    private void process(ParticipateVote vote) {
        TerminateParticipateVoteRequestDto requestDto = TerminateParticipateVoteRequestDto
                        .builder()
                        .postId(vote.getPost().getId())
                        .build();

        updateParticipateVoteUseCase.terminate(requestDto);
    }
}
