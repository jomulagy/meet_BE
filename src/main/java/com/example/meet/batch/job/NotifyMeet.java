package com.example.meet.batch.job;

import com.example.meet.api.message.application.port.in.SendMessageUseCase;
import com.example.meet.api.participate.application.domain.entity.ParticipateVote;
import com.example.meet.api.participate.application.domain.entity.ParticipateVoteItem;
import com.example.meet.api.post.application.domain.entity.Post;
import com.example.meet.api.post.application.port.out.GetPostPort;
import com.example.meet.batch.CommonJob;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.repository.BatchLogRepository;
import java.util.List;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

public class NotifyMeet extends CommonJob {
    private final GetPostPort getPostPort;
    private final SendMessageUseCase sendMessageUseCase;

    public NotifyMeet(
            BatchLogRepository batchLogRepository,
            GetPostPort getPostPort,
            SendMessageUseCase sendMessageUseCase) {
        super(batchLogRepository);
        this.getPostPort = getPostPort;
        this.sendMessageUseCase = sendMessageUseCase;
    }

    @Override
    @Transactional
    protected String performJob(JobExecutionContext context) {
        Long postId = context.getJobDetail().getJobDataMap().getLong("postId");
        Post post = getPostPort.getPostById(postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEET_NOT_EXISTS));

        ParticipateVote participateVote = post.getParticipateVote();
        if (participateVote == null) {
            return post.getTitle() + " 모임 알림 대상 없음";
        }

        int sentCount = 0;
        List<ParticipateVoteItem> items = participateVote.getParticipateVoteItems();
        for (ParticipateVoteItem item : items) {
            if (!Boolean.TRUE.equals(item.getIsParticipate())) {
                continue;
            }
            for (var member : item.getParticipateVoters()) {
                sendMessageUseCase.sendMeetNotification(post.getTitle(), post.getId(), member);
                sentCount++;
            }
        }

        return post.getTitle() + " 모임 일주일 전 알림 전송 완료 (" + sentCount + "명)";
    }
}
