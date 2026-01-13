package com.example.meet.batch.application.service;

import com.example.meet.batch.application.port.in.RegisterJobUseCase;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.api.participate.application.domain.entity.ParticipateVote;
import com.example.meet.api.vote.application.domain.entity.Vote;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

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
}
