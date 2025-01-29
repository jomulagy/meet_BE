package com.example.meet.batch;

import com.example.meet.batch.job.CreateRoutineMeet;
import com.example.meet.batch.job.DeleteMemberPrevillege;
import com.example.meet.batch.job.NotifyMeet;
import com.example.meet.batch.job.RefreshAdminKakaoAcessToken;
import com.example.meet.batch.job.SendDepositMessage;
import com.example.meet.batch.job.SendDepositWarningMessage;
import com.example.meet.batch.job.SendParticipateMessage;
import com.example.meet.batch.job.TerminateParticipateVote;
import com.example.meet.batch.job.TerminatePlaceVote;
import com.example.meet.batch.job.TerminateScheduleVote;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;

@Getter
@RequiredArgsConstructor
public enum ScheduledJob {
    REFRESH_ADMIN_KAKAO_ACCESS_TOKEN(RefreshAdminKakaoAcessToken.class, "0 0 0 * * ? *"),
    DELETE_MEMBER_PREVILLEGE(DeleteMemberPrevillege.class, "0 5 0 11 1 ? *"),
    TERMINATE_SCHEDULE_VOTE(TerminateScheduleVote.class, "0 10 0 * * ? *"),
    TERMINATE_PLACE_VOTE(TerminatePlaceVote.class, "0 15 0 * * ? *"),
    TERMINATE_PARTICIPATE_VOTE(TerminateParticipateVote.class, "0 20 0 * * ? *"),
    CREATE_ROUTINE_MEET(CreateRoutineMeet.class, "0 0 8 1 3,6,9,12 ? *"),
    SEND_PARTICIPATE_MESSAGE(SendParticipateMessage.class, "0 30 08 * * ? *"),
    SEND_DEPOSIT_MESSAGE(SendDepositMessage.class, "0 35 08 25 * ? *"),
    SEND_DEPOSIT_WARNING_MESSAGE(SendDepositWarningMessage.class,"0 35 08 8 * ? *"),
    NOTIFY_MEET(NotifyMeet.class, "0 0 9 * * ? *")
    ;

    private final Class<? extends Job> jobClass;
    private final String cronExpression;
}
