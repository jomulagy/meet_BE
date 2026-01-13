package com.example.meet.api.vote.application.port.in;

import com.example.meet.api.post.application.domain.entity.Post;
import com.example.meet.api.vote.adapter.in.dto.in.FindVoteRequestDto;
import com.example.meet.api.vote.adapter.in.dto.out.FindVoteResponseDto;
import com.example.meet.api.vote.application.domain.entity.Vote;

import java.util.List;

public interface GetVoteUseCase {
    List<FindVoteResponseDto> getFindVoteResponseDtoList(FindVoteRequestDto inDto);

    FindVoteResponseDto getResponseDto(Long voteId);

    List<Vote> getVoteListByPost(Post post);

    Vote getVote(Long voteId);
}
