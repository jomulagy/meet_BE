package com.example.meet.vote.application.port.in;

import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.vote.adapter.in.dto.in.FindVoteItemRequestDto;
import com.example.meet.vote.adapter.in.dto.in.FindVoteRequestDto;
import com.example.meet.vote.adapter.in.dto.out.FindVoteItemResponseDto;
import com.example.meet.vote.adapter.in.dto.out.FindVoteResponseDto;
import com.example.meet.vote.application.domain.vo.VoteResult;
import java.util.List;

public interface GetVoteUseCase {
    FindVoteResponseDto get(FindVoteRequestDto inDto);

    List<FindVoteItemResponseDto> getItemList(FindVoteItemRequestDto inDto);

    VoteResult getVote(Meet meet);

    VoteResult getActiveVote(Meet meet);
}
