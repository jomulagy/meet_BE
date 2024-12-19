package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.common.dto.TemplateArgs;
import com.example.meet.common.enumulation.Message;
import com.example.meet.common.utils.MessageManager;
import com.example.meet.entity.Member;
import com.example.meet.repository.BatchLogRepository;
import com.example.meet.repository.MemberRepository;
import java.time.LocalDate;
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
    protected String performJob(JobExecutionContext context) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        StringBuilder log = new StringBuilder();

        log.append("[");

        List<Member> memberList = memberRepository.findByDeposit(false);

        TemplateArgs templateArgs = TemplateArgs.builder()
                .year(String.valueOf(year))
                .build();
        Message.DEPOSIT_WARNING.setTemplateArgs(templateArgs);

        for(Member member : memberList){
            if (member.getId().equals(2927398983L)) {
                messageManager.sendMe(Message.DEPOSIT_WARNING).block();
            } else {
                messageManager.send(Message.DEPOSIT_WARNING, member).block();
            }

            log.append(member.getName());
            log.append(", ");
        }

        // 마지막 ", " 제거
        int index = log.lastIndexOf( ", ");

        if (index != -1 && index == log.length() - 2) {
            log.delete(index, log.length());
        }

        log.append("]");

        return log.toString();
    }
}
