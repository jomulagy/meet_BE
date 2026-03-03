package com.example.meet.batch.job;

import com.example.meet.api.attendance.application.domain.entity.Penalty;
import com.example.meet.api.attendance.application.port.out.GetPenaltyPort;
import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.api.member.application.port.out.GetMemberPort;
import com.example.meet.batch.CommonJob;
import com.example.meet.infrastructure.dto.TemplateArgs;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.Message;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.repository.BatchLogRepository;
import com.example.meet.infrastructure.repository.MemberRepository;
import com.example.meet.infrastructure.utils.MessageManager;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

public class CheckDepositStatus extends CommonJob {
    private final GetMemberPort getMemberPort;
    private final GetPenaltyPort getPenaltyPort;
    private final MemberRepository memberRepository;
    private final MessageManager messageManager;

    public CheckDepositStatus(BatchLogRepository batchLogRepository,
                              GetMemberPort getMemberPort,
                              GetPenaltyPort getPenaltyPort,
                              MemberRepository memberRepository,
                              MessageManager messageManager) {
        super(batchLogRepository);
        this.getMemberPort = getMemberPort;
        this.getPenaltyPort = getPenaltyPort;
        this.memberRepository = memberRepository;
        this.messageManager = messageManager;
    }

    @Override
    @Transactional
    protected String performJob(JobExecutionContext context) {
        Long memberId = context.getJobDetail().getJobDataMap().getLong("memberId");
        Long postId = context.getJobDetail().getJobDataMap().getLong("postId");
        String memberName = context.getJobDetail().getJobDataMap().getString("memberName");

        Member member = getMemberPort.getMemberById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS));

        Penalty penalty = getPenaltyPort.findByMemberIdAndPostId(memberId, postId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PENALTY_NOT_EXISTS));

        if (!penalty.getPenaltyPaid()) {
            member.deny();
            memberRepository.save(member);

            TemplateArgs templateArgs = TemplateArgs.builder()
                    .memberName(memberName)
                    .build();

            Message.PENALTY_BLOCK_ADMIN.setTemplateArgs(templateArgs);
            messageManager.send(Message.PENALTY_BLOCK_ADMIN, member).block();

            return memberName + " 벌금 미납 확인 - 권한 차단 완료";
        }

        return memberName + " 벌금 납부 확인 완료";
    }
}
