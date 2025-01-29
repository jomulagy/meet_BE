package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.common.enumulation.DepositStatus;
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
    protected void performJob(JobExecutionContext context) {
        List<Member> memberList = memberRepository.findAll();

        for(Member member: memberList) {
            if (!member.hasDeposit()) {
                continue;
            }

            member.setIsDepositFalse();
            member.setPrivilege(MemberPrevillege.denied);

            taskList.add(member.getName());
        }

        memberRepository.saveAll(memberList);
    }
}
