package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.common.dto.TemplateArgs;
import com.example.meet.common.enumulation.MemberPrevillege;
import com.example.meet.common.enumulation.Message;
import com.example.meet.common.utils.MessageManager;
import com.example.meet.entity.Member;
import com.example.meet.repository.BatchLogRepository;
import com.example.meet.repository.MemberRepository;
import org.quartz.JobExecutionContext;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public class DeleteMemberPrevillege extends CommonJob {
    private final MemberRepository memberRepository;
    private final MessageManager messageManager;

    public DeleteMemberPrevillege(BatchLogRepository batchLogRepository,
                                  MemberRepository memberRepository,
                                  MessageManager messageManager) {
        super(batchLogRepository);
        this.memberRepository = memberRepository;
        this.messageManager = messageManager;
    }

    @Override
    @Transactional
    protected void performJob(JobExecutionContext context) {
        List<Member> memberList = memberRepository.findMembersWithDepositIsDepositFalse();

        TemplateArgs templateArgs = TemplateArgs.builder()
                .build();
        Message.DELETE_PRIVILEGE.setTemplateArgs(templateArgs);

        for(Member member: memberList) {
            member.setIsDepositFalse();
            member.setPrivilege(MemberPrevillege.denied);

            if (member.getId().equals(2927398983L)) {
                messageManager.sendMe(Message.DELETE_PRIVILEGE).block();
            } else {
                messageManager.send(Message.DELETE_PRIVILEGE, member).block();
            }

            taskList.add(member.getName());
        }

        memberRepository.saveAll(memberList);
    }
}
