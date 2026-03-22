package com.example.meet.batch.job;

import com.example.meet.api.message.application.port.in.SendMessageUseCase;
import com.example.meet.api.vote.application.domain.entity.Vote;
import com.example.meet.api.vote.application.port.out.GetVotePort;
import com.example.meet.batch.CommonJob;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.repository.BatchLogRepository;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

public class SendVoteEndReminder extends CommonJob {
    private final GetVotePort getVotePort;
    private final SendMessageUseCase sendMessageUseCase;

    public SendVoteEndReminder(BatchLogRepository batchLogRepository,
                               GetVotePort getVotePort,
                               SendMessageUseCase sendMessageUseCase) {
        super(batchLogRepository);
        this.getVotePort = getVotePort;
        this.sendMessageUseCase = sendMessageUseCase;
    }

    @Override
    @Transactional
    protected String performJob(JobExecutionContext context) {
        try {
            Long voteId = context.getJobDetail().getJobDataMap().getLong("voteId");

            Vote vote = getVotePort.get(voteId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.VOTE_NOT_EXISTS));

            sendMessageUseCase.sendVoteEndReminder(
                    vote.getPost().getTitle() + " : " + vote.getTitle(),
                    vote.getPost().getId().toString()
            );

            return vote.getTitle() + " 투표 종료 30분 전 알림 전송 완료";
        } catch (Exception e) {
            super.insertBatch("Fail to send vote end reminder", "FAILURE", e.getMessage());
            throw e;
        }
    }
}
