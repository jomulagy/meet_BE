package com.example.meet.vote.application.port.in;

import com.example.meet.vote.application.adapter.in.dto.in.CreateVoteItemRequestDto;
import com.example.meet.vote.application.adapter.in.dto.in.DeleteVoteItemRequestDto;
import com.example.meet.vote.application.adapter.in.dto.in.FindVoteItemRequestDto;
import com.example.meet.vote.application.adapter.in.dto.in.FindVoteRequestDto;
import com.example.meet.vote.application.adapter.in.dto.in.UpdateVoteRequestDto;
import com.example.meet.vote.application.adapter.in.dto.out.CreateVoteItemResponseDto;
import com.example.meet.vote.application.adapter.in.dto.out.DeleteVoteItemResponseDto;
import com.example.meet.vote.application.adapter.in.dto.out.FindVoteItemResponseDto;
import com.example.meet.vote.application.adapter.in.dto.out.FindVoteResponseDto;
import com.example.meet.vote.application.adapter.in.dto.out.UpdateVoteResponseDto;
import java.util.List;

public interface VoteUseCase {
    FindVoteResponseDto findVote(FindVoteRequestDto inDto);

    List<FindVoteItemResponseDto> findVoteItems(FindVoteItemRequestDto inDto);

    CreateVoteItemResponseDto createVoteItem(CreateVoteItemRequestDto inDto);

    DeleteVoteItemResponseDto deleteVoteItem(DeleteVoteItemRequestDto inDto);

    UpdateVoteResponseDto updateVote(UpdateVoteRequestDto inDto);
}
