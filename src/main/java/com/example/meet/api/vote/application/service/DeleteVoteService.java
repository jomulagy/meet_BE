package com.example.meet.api.vote.application.service;

import com.example.meet.api.vote.adapter.in.dto.out.DeleteVoteResponseDto;
import com.example.meet.api.vote.application.port.in.DeleteVoteUseCase;
import com.example.meet.api.vote.application.port.out.DeleteVotePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteVoteService implements DeleteVoteUseCase {
    private final DeleteVotePort deleteVotePort;

    @Override
    public DeleteVoteResponseDto deleteVote(Long voteId) {
        deleteVotePort.delete(voteId);

        return DeleteVoteResponseDto.builder()
                .deletedId(voteId)
                .build();
    }
}
