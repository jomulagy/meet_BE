package com.example.meet.vote.application.service;

import com.example.meet.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.entity.Member;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.utils.DateTimeUtils;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.meet.application.port.in.GetMeetUseCase;
import com.example.meet.vote.adapter.in.dto.in.FindVoteRequestDto;
import com.example.meet.vote.adapter.in.dto.out.FindVoteResponseDto;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.port.in.GetVoteUseCase;
import com.example.meet.vote.application.domain.vo.VoteResult;
import com.example.meet.vote.application.port.out.GetVotePort;
import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetVoteService implements GetVoteUseCase {

    private final GetLogginedInfoUseCase getLogginedInfoUseCase;
    private final GetMeetUseCase getMeetUseCase;
    private final GetVotePort getVotePort;

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public FindVoteResponseDto get(FindVoteRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();
        Meet meet = getMeetUseCase.get(inDto.getMeetId());
        Vote vote = getVotePort.getByMeetId(meet.getId()).orElse(null);

        if(vote == null) {
            return null;
        }

        String endDate = vote.getEndDate() != null ? DateTimeUtils.formatWithOffset(vote.getEndDate()) : null;
        boolean isAuthor = meet.getAuthor().equals(user);

        return FindVoteResponseDto.builder()
                .meetTitle(meet.getTitle())
                .endDate(endDate)
                .isAuthor(Boolean.toString(isAuthor))
                .build();
    }

    @Override
    public VoteResult getVote(Meet meet) {
        Vote vote = getVotePort.getByMeetId(meet.getId()).orElse(null);
        return new VoteResult(vote);
    }

    @Override
    public VoteResult getActiveVote(Meet meet) {
        validateVoteIsActive(meet);
        return getVote(meet);
    }

    private void validateVoteIsActive(Meet meet) {
        LocalDateTime endDate = meet.getEndDate();
        if (endDate != null && endDate.isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.SCHEDULE_VOTE_END);
        }
    }
}
