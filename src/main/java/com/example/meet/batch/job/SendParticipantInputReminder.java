package com.example.meet.batch.job;

import com.example.meet.api.message.application.port.in.SendMessageUseCase;
import com.example.meet.batch.CommonJob;
import com.example.meet.infrastructure.dto.TemplateArgs;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.Message;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.utils.MessageManager;
import com.example.meet.api.post.application.domain.entity.Post;
import com.example.meet.api.post.application.port.out.GetPostPort;
import com.example.meet.infrastructure.repository.BatchLogRepository;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

public class SendParticipantInputReminder extends CommonJob {
    private final GetPostPort getPostPort;
    private final SendMessageUseCase sendMessageUseCase;

    public SendParticipantInputReminder(BatchLogRepository batchLogRepository,
                                        GetPostPort getPostPort,
                                        SendMessageUseCase sendMessageUseCase) {
        super(batchLogRepository);
        this.getPostPort = getPostPort;
        this.sendMessageUseCase = sendMessageUseCase;
    }

    @Override
    @Transactional
    protected String performJob(JobExecutionContext context) {
        try {
            Long postId = context.getJobDetail().getJobDataMap().getLong("postId");

            Post post = getPostPort.getPostById(postId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEET_NOT_EXISTS));

            sendMessageUseCase.sendParticipantInputReminder(post.getTitle(), post.getId());

            return post.getTitle() + " 참여자 입력 알림 전송 완료";
        } catch (Exception e) {
            super.insertBatch("Fail to send participant input reminder", "FAILURE", e.getMessage());
            throw e;
        }
    }
}
