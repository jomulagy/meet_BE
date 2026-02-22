package com.example.meet.batch.application.service;

import com.example.meet.batch.application.port.in.RegisterJobUseCase;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.api.participate.application.domain.entity.ParticipateVote;
import com.example.meet.api.vote.application.domain.entity.Vote;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RegisterJobService implements RegisterJobUseCase {
    private final Scheduler scheduler;

    @Override
    public void terminateVote(Vote vote) {
        LocalDateTime endDate = vote.getEndDate().plusDays(1).toLocalDate().atTime(0,0,0);

        String cronExpression = String.format("%d %d %d %d %d ? %d",
                endDate.getSecond(),
                endDate.getMinute(),
                endDate.getHour(),
                endDate.getDayOfMonth(),
                endDate.getMonthValue(),
                endDate.getYear());

        JobDetail jobDetail = JobBuilder.newJob(com.example.meet.batch.job.TerminateVote.class)
                .withIdentity("TerminateVote_" + vote.getId())
                .usingJobData("voteId", vote.getId())
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("TerminateVoteTrigger_" + vote.getId())
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void terminateParticipateVote(ParticipateVote vote) {
        LocalDateTime endDate = vote.getEndDate().plusDays(1).toLocalDate().atTime(0,0,0);

        String cronExpression = String.format("%d %d %d %d %d ? %d",
                endDate.getSecond(),
                endDate.getMinute(),
                endDate.getHour(),
                endDate.getDayOfMonth(),
                endDate.getMonthValue(),
                endDate.getYear());

        JobDetail jobDetail = JobBuilder.newJob(com.example.meet.batch.job.TerminateVote.class)
                .withIdentity("TerminateParticipateVote_" + vote.getId())
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("TerminateParticipateTrigger_" + vote.getId())
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void updateParticipants(Long postId, LocalDate meetDate) {
        // 모임날짜의 다음날 12시
        LocalDateTime reminderDate = meetDate.plusDays(1).atTime(12, 0, 0);

        String cronExpression = String.format("%d %d %d %d %d ? %d",
                reminderDate.getSecond(),
                reminderDate.getMinute(),
                reminderDate.getHour(),
                reminderDate.getDayOfMonth(),
                reminderDate.getMonthValue(),
                reminderDate.getYear());

        JobDetail jobDetail = JobBuilder.newJob(com.example.meet.batch.job.SendParticipantInputReminder.class)
                .withIdentity("SendParticipantInputReminder_" + postId)
                .usingJobData("postId", postId)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("SendParticipantInputReminderTrigger_" + postId)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void checkDepositStatus(Long memberId, Long postId, String postTitle, String memberName, LocalDate checkDate) {
        LocalDateTime checkDateTime = checkDate.atTime(10, 0, 0); // 오전 10시

        String cronExpression = String.format("%d %d %d %d %d ? %d",
                checkDateTime.getSecond(),
                checkDateTime.getMinute(),
                checkDateTime.getHour(),
                checkDateTime.getDayOfMonth(),
                checkDateTime.getMonthValue(),
                checkDateTime.getYear());

        JobDetail jobDetail = JobBuilder.newJob(com.example.meet.batch.job.CheckDepositStatus.class)
                .withIdentity("CheckDepositStatus_" + memberId + "_" + postId + "_" + checkDate)
                .usingJobData("memberId", memberId)
                .usingJobData("postId", postId)
                .usingJobData("postTitle", postTitle)
                .usingJobData("memberName", memberName)
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity("CheckDepositStatusTrigger_" + memberId + "_" + postId + "_" + checkDate)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();

        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void cancelCheckDepositStatus(Long memberId, Long postId, LocalDate checkDate) {
        String jobIdentity = "CheckDepositStatus_" + memberId + "_" + postId + "_" + checkDate;

        try {
            JobKey jobKey = JobKey.jobKey(jobIdentity);
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
        } catch (SchedulerException e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
