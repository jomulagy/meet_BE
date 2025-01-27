package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.common.dto.TemplateArgs;
import com.example.meet.common.enumulation.DepositStatus;
import com.example.meet.common.enumulation.Message;
import com.example.meet.common.utils.MessageManager;
import com.example.meet.entity.Deposit;
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
    protected String performJob(JobExecutionContext context) {
        LocalDate now = LocalDate.now();
        int year = now.getYear();

        List<Member> memberList = memberRepository.findAll();

        for(Member member : memberList){
            Deposit deposit = member.getDeposit();
            if(deposit != null){
                deposit.setIsDepositFalse();
            }
        }

        TemplateArgs templateArgs = TemplateArgs.builder()
                .year(String.valueOf(year))
                .nextYear(String.valueOf(year+1))
                .build();
        Message.DEPOSIT.setTemplateArgs(templateArgs);
        messageManager.sendAll(Message.DEPOSIT).block();
        messageManager.sendMe(Message.DEPOSIT).block();

        return "전송 완료";
    }
}
