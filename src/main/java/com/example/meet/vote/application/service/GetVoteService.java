package com.example.meet.vote.application.service;

import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.utils.DateTimeUtils;
import com.example.meet.post.application.domain.entity.Post;
import com.example.meet.vote.adapter.in.dto.in.FindVoteRequestDto;
import com.example.meet.vote.adapter.in.dto.out.FindVoteResponseDto;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.port.in.GetVoteUseCase;
import com.example.meet.vote.application.port.out.GetVotePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetVoteService implements GetVoteUseCase {
    private final GetVotePort getVotePort;

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public List<FindVoteResponseDto> getFindVoteResponseDtoList(FindVoteRequestDto inDto) {
        List<FindVoteResponseDto> responseDtoList = new ArrayList<>();

        List<Vote> voteList = getVotePort.getByPostId(inDto.getPostId());

        for(Vote vote : voteList) {
            String endDate = vote.getEndDate() != null ? DateTimeUtils.formatWithOffset(vote.getEndDate()) : null;

            responseDtoList.add(
                    FindVoteResponseDto.builder()
                    .title(vote.getTitle())
                    .result(vote.getResult())
                    .endDate(endDate)
                    .build()
            );
        }

        return responseDtoList;
    }

    @Override
    public List<Vote> getVoteListByPost(Post post) {
        return getVotePort.getByPostId(post.getId());
    }

    @Override
    public Vote getVote(Long voteId) {
        return getVotePort.get(voteId).orElseThrow(() -> new BusinessException(ErrorCode.MEET_NOT_EXISTS));
    }
}
