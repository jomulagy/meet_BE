package com.example.meet.vote.application.service;

import com.example.meet.batch.application.port.in.RegisterJobUseCase;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.message.application.port.in.SendMessageUseCase;
import com.example.meet.post.application.domain.entity.Post;
import com.example.meet.post.application.port.out.GetPostPort;
import com.example.meet.vote.adapter.in.dto.in.CreateVoteRequestDto;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.port.in.CreateVoteUseCase;
import com.example.meet.vote.application.port.out.CreateVotePort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreateVoteService implements CreateVoteUseCase {
    private final GetPostPort getPostPort;
    private final CreateVotePort createVotePort;
    private final RegisterJobUseCase registerJobUseCase;

    private final SendMessageUseCase sendMessageUseCase;

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAdminAccess(authentication)")
    @Transactional
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

        sendMessageUseCase.sendVoteCreated(vote.getPost().getTitle() + " : " + vote.getTitle(), vote.getPost().getId().toString());

        Vote savedVote = createVotePort.create(vote);

        registerJobUseCase.terminateVote(savedVote);
    }
}
