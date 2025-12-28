package com.example.meet.batch;

import com.example.meet.batch.job.RefreshAdminKakaoAcessToken;
import com.example.meet.batch.job.TerminateParticipateVote;
import com.example.meet.batch.job.TerminateVote;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;

@Getter
@RequiredArgsConstructor
public enum ScheduledJob {
//    RERUN_FAILED_JOBS(RerunFailedJobs.class, "0 0 0 * * ? *"),
    REFRESH_ADMIN_KAKAO_ACCESS_TOKEN(RefreshAdminKakaoAcessToken.class, "0 5 0 * * ? *"),
//    DELETE_MEMBER_PREVILLEGE(DeleteMemberPrevillege.class, "0 10 0 11 1 ? *"),
    TERMINATE_VOTE(TerminateVote.class, "0 15 0 * * ? *"),
    TERMINATE_PARTICIPATE_VOTE(TerminateParticipateVote.class, "0 17 0 * * ? *"),
//    CREATE_ROUTINE_MEET(CreateRoutineMeet.class, "0 39 21 24 8 ? *"),
//    SEND_DEPOSIT_MESSAGE(SendDepositMessage.class, "0 35 08 25 12 ? *"),
//    SEND_DEPOSIT_WARNING_MESSAGE(SendDepositWarningMessage.class,"0 35 08 8 1 ? *"),
    ;

    private final Class<? extends Job> jobClass;
    private final String cronExpression;
}
