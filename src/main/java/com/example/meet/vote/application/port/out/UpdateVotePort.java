package com.example.meet.vote.application.port.out;

import com.example.meet.entity.Member;
import com.example.meet.vote.adapter.in.dto.in.UpdateVoteItemRequestDto;

import java.util.List;

public interface UpdateVotePort {
    void updateVoteItems(Long voteId, Member voter, List<UpdateVoteItemRequestDto> dtoList);
}
