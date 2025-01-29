package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.common.dto.TemplateArgs;
import com.example.meet.common.enumulation.Message;
import com.example.meet.common.utils.MessageManager;
import com.example.meet.entity.Member;
import com.example.meet.repository.BatchLogRepository;
import com.example.meet.repository.MemberRepository;
import java.time.LocalDate;
import org.quartz.JobExecutionContext;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

public class SendDepositMessage extends CommonJob {
    private final MemberRepository memberRepository;
    private final MessageManager messageManager;

    public SendDepositMessage(BatchLogRepository batchLogRepository,
            MemberRepository memberRepository, MessageManager messageManager) {
        super(batchLogRepository);
        this.memberRepository = memberRepository;
        this.messageManager = messageManager;
    }

    @Override
    @Transactional
    protected void performJob(JobExecutionContext context) {
        LocalDate now = LocalDate.now();
        LocalDate next = now.plusMonths(1);

        List<Member> memberList = memberRepository.findMembersWithPrivilegeEndDateOnNextMonth10();

        TemplateArgs templateArgs = TemplateArgs.builder()
                .year(String.valueOf(now.getYear()))
                .month(String.valueOf(now.getMonthValue()))
                .nextYear(String.valueOf(next.getYear()))
                .nextMonth(String.valueOf(next.getMonthValue()))
                .build();
        Message.DEPOSIT.setTemplateArgs(templateArgs);

        for(Member member : memberList){
            member.setIsDepositFalse();

            if (member.getId().equals(2927398983L)) {
                messageManager.sendMe(Message.DEPOSIT).block();
            } else {
                messageManager.send(Message.DEPOSIT, member).block();
            }
            taskList.add(member.getName());
        }
    }
}
