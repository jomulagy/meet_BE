package com.example.meet.participate.application.service;

import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.participate.application.domain.entity.ParticipateVote;
import com.example.meet.participate.application.domain.entity.ParticipateVoteItem;
import com.example.meet.participate.application.port.in.CreateParticipateUseCase;
import com.example.meet.participate.application.port.out.CreateParticipateVoteItemPort;
import com.example.meet.participate.application.port.out.CreateParticipateVotePort;
import com.example.meet.post.application.domain.entity.Post;
import com.example.meet.post.application.port.in.GetPostUseCase;
import com.example.meet.vote.application.domain.entity.Vote;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateParticipateService implements CreateParticipateUseCase {
    private final GetPostUseCase getPostUseCase;
    private final CreateParticipateVotePort createParticipateVotePort;
    private final CreateParticipateVoteItemPort createParticipateVoteItemPort;
    private final Scheduler scheduler;

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAdminAccess(authentication)")
    public void create(Long postId) {
        Post post = getPostUseCase.getEntity(postId);

        ParticipateVote participateVote = ParticipateVote.builder()
                .totalNum(0)
                .endDate(LocalDateTime.now().plusDays(4))
                .post(post)
                .build();

        ParticipateVote saved = createParticipateVotePort.create(participateVote);

        addVoteItems(saved);

        registerTerminateVoteJob(saved);
    }

    private void addVoteItems(ParticipateVote saved) {

        createParticipateVoteItemPort.create(
                ParticipateVoteItem.builder()
                        .isParticipate(true)
                        .participateVote(saved)
                        .build()
        );

        createParticipateVoteItemPort.create(
                ParticipateVoteItem.builder()
                        .isParticipate(false)
                        .participateVote(saved)
                        .build()
        );

    }

    private void registerTerminateVoteJob(ParticipateVote vote) {
        LocalDateTime endDate = vote.getEndDate();

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
