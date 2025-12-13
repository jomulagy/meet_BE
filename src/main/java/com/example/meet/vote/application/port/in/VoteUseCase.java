package com.example.meet.vote.application.port.in;

import com.example.meet.vote.adapter.in.dto.in.FindVoteRequestDto;
import com.example.meet.vote.adapter.in.dto.in.UpdateVoteRequestDto;
import com.example.meet.vote.adapter.in.dto.out.FindVoteResponseDto;
import com.example.meet.vote.adapter.in.dto.out.UpdateVoteResponseDto;

public interface VoteUseCase {
    FindVoteResponseDto get(FindVoteRequestDto inDto);

    UpdateVoteResponseDto vote(UpdateVoteRequestDto inDto);
}
