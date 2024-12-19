package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.common.enumulation.MemberPrevillege;
import com.example.meet.entity.Member;
import com.example.meet.repository.BatchLogRepository;
import com.example.meet.repository.MemberRepository;
import org.quartz.JobExecutionContext;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

public class DeleteMemberPrevillege extends CommonJob {
    private final MemberRepository memberRepository;

    public DeleteMemberPrevillege(BatchLogRepository batchLogRepository,
            MemberRepository memberRepository) {
        super(batchLogRepository);
        this.memberRepository = memberRepository;
    }

    @Override
    @Transactional
    protected String performJob(JobExecutionContext context) {
        StringBuilder log = new StringBuilder();
        List<Member> memberList = memberRepository.findByDeposit(false);

        log.append("[");

        for(Member member: memberList){
            member.setPrevillege(MemberPrevillege.denied);

            log.append(member.getName());
            log.append(", ");
        }

        // 마지막 ", " 제거
        int index = log.lastIndexOf( ", ");

        if (index != -1 && index == log.length() - 2) {
            log.delete(index, log.length());
        }

        log.append("]");

        memberRepository.saveAll(memberList);

        return log.toString();
    }
}
