package com.example.meet.batch.job;

import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.api.member.application.port.out.GetMemberPort;
import com.example.meet.batch.CommonJob;
import com.example.meet.infrastructure.dto.TemplateArgs;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.Message;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.repository.BatchLogRepository;
import com.example.meet.infrastructure.utils.MessageManager;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

public class CheckDepositStatus extends CommonJob {
    private final GetMemberPort getMemberPort;
    private final MessageManager messageManager;

    public CheckDepositStatus(BatchLogRepository batchLogRepository,
                             GetMemberPort getMemberPort,
                             MessageManager messageManager) {
        super(batchLogRepository);
        this.getMemberPort = getMemberPort;
        this.messageManager = messageManager;
    }

    @Override
    @Transactional
    protected String performJob(JobExecutionContext context) {
        try {
            Long memberId = context.getJobDetail().getJobDataMap().getLong("memberId");
            Long postId = context.getJobDetail().getJobDataMap().getLong("postId");
            String postTitle = context.getJobDetail().getJobDataMap().getString("postTitle");
            String memberName = context.getJobDetail().getJobDataMap().getString("memberName");

            Member member = getMemberPort.getMemberById(memberId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS));

            // 관리자(배치 실행자)에게 입금 확인 메시지 전송
            TemplateArgs templateArgs = TemplateArgs.builder()
                    .title(postTitle)
                    .memberName(memberName)
                    .but(String.valueOf(postId))
                    .build();

            Message.DEPOSIT_CHECK_ADMIN.setTemplateArgs(templateArgs);
            messageManager.sendMe(Message.DEPOSIT_CHECK_ADMIN).block();

            return memberName + " 벌금 입금 확인 요청 전송 완료 (모임: " + postTitle + ")";
        } catch (Exception e) {
            super.insertBatch("Fail to send deposit check message", "FAILURE", e.getMessage());
            throw e;
        }
    }
}
