package com.example.meet.vote.application.service;

import com.example.meet.batch.application.port.in.RegisterJobUseCase;
import com.example.meet.infrastructure.dto.TemplateArgs;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.Message;
import com.example.meet.infrastructure.enumulation.VoteType;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.utils.MessageManager;
import com.example.meet.post.application.domain.entity.Post;
import com.example.meet.post.application.port.out.GetPostPort;
import com.example.meet.vote.adapter.in.dto.in.CreateVoteRequestDto;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.port.in.CreateVoteUseCase;
import com.example.meet.vote.application.port.out.CreateVotePort;
import lombok.RequiredArgsConstructor;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreateVoteService implements CreateVoteUseCase {
    private final GetPostPort getPostPort;
    private final CreateVotePort createVotePort;
    private final RegisterJobUseCase registerJobUseCase;

    private final MessageManager messageManager;

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAdminAccess(authentication)")
    public void create(CreateVoteRequestDto request) {
        Optional<Post> post = getPostPort.getPostById(request.getPostId());

        if(post.isEmpty()) {
            throw new BusinessException(ErrorCode.MEET_NOT_EXISTS);
        }

        Vote vote = Vote.builder()
                .title(request.getTitle())
                .endDate(LocalDate.parse(request.getVoteDeadline(), DateTimeFormatter.ISO_LOCAL_DATE).atTime(LocalTime.of(0, 0)))
                .activeYn(true)
                .type(request.getVoteType())
                .isDuplicate(request.getDuplicateYn().equals("Y"))
                .post(post.get())
                .build();

        TemplateArgs templateArgs = TemplateArgs.builder()
                .title(vote.getPost().getTitle() + " : " + vote.getTitle())
                .but(vote.getPost().getId().toString())
                .scheduleType(null)
                .build();

        Message.VOTE.setTemplateArgs(templateArgs);
        messageManager.sendAll(Message.VOTE).block();
        messageManager.sendMe(Message.VOTE).block();

        Vote savedVote = createVotePort.create(vote);

        registerJobUseCase.terminateVote(savedVote);
    }
}
