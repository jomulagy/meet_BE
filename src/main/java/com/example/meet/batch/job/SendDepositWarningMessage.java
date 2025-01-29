package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.common.dto.TemplateArgs;
import com.example.meet.common.enumulation.Message;
import com.example.meet.common.utils.MessageManager;
import com.example.meet.entity.Member;
import com.example.meet.repository.BatchLogRepository;
import com.example.meet.repository.MemberRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.quartz.JobExecutionContext;

public class SendDepositWarningMessage extends CommonJob {
    private final MemberRepository memberRepository;
    private final MessageManager messageManager;

    public SendDepositWarningMessage(BatchLogRepository batchLogRepository,
            MemberRepository memberRepository, MessageManager messageManager) {
        super(batchLogRepository);
        this.memberRepository = memberRepository;
        this.messageManager = messageManager;
    }

    @Override
    protected void performJob(JobExecutionContext context) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();

        List<Member> memberList = memberRepository.findMembersWithPrivilegeEndDateOnCurMonth10();

        TemplateArgs templateArgs = TemplateArgs.builder()
                .year(String.valueOf(year))
                .nextYear(String.valueOf(year+1))
                .build();
        Message.DEPOSIT.setTemplateArgs(templateArgs);

        for(Member member : memberList){
            if (member.getId().equals(2927398983L)) {
                messageManager.sendMe(Message.DEPOSIT).block();
            } else {
                messageManager.send(Message.DEPOSIT, member).block();
            }

            taskList.add(member.getName());
        }
    }
}
