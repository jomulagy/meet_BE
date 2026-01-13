package com.example.meet.api.vote.adapter.in;

import com.example.meet.api.vote.adapter.in.dto.in.*;
import com.example.meet.api.vote.adapter.in.dto.out.DeleteVoteItemResponseDto;
import com.example.meet.api.vote.adapter.in.dto.out.FindVoteResponseDto;
import com.example.meet.api.vote.adapter.in.dto.out.TerminateResponseDto;
import com.example.meet.api.vote.application.port.in.*;
import com.example.meet.infrastructure.CommonResponse;
import com.example.meet.api.vote.adapter.in.dto.out.DeleteVoteResponseDto;
import com.example.meet.vote.adapter.in.dto.in.*;
import com.example.meet.vote.adapter.in.dto.out.*;
import com.example.meet.vote.application.port.in.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.lang.Long.parseLong;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vote")
public class VoteController {

    private final GetVoteUseCase getVoteUseCase;
    private final CreateVoteUseCase createVoteUseCase;
    private final CreateVoteItemUseCase createVoteItemUseCase;
    private final DeleteVoteItemUseCase deleteVoteItemUseCase;
    private final DeleteVoteUseCase deleteVoteUseCase;
    private final UpdateVoteUseCase updateVoteUseCase;

    @GetMapping("/list")
    public CommonResponse<List<FindVoteResponseDto>> findVoteList(@RequestParam(name = "postId") Long meetId) {
        FindVoteRequestDto inDto = FindVoteRequestDto.builder()
                .postId(meetId)
                .build();

        return CommonResponse.success(getVoteUseCase.getFindVoteResponseDtoList(inDto));
    }

    @PostMapping("/item")
    public CommonResponse<FindVoteResponseDto> createVoteItem(@RequestBody CreateVoteItemRequestDto request) {
        return CommonResponse.success(createVoteItemUseCase.createItem(request));
    }

    @PostMapping("")
    public CommonResponse<Void> createVote(@RequestBody CreateVoteRequestDto request) {
        createVoteUseCase.create(request);
        return CommonResponse.success();
    }

    @PostMapping("/confirm")
    public CommonResponse<FindVoteResponseDto> confirm(@RequestBody UpdateVoteRequestDto request) {
        return CommonResponse.success(updateVoteUseCase.vote(request));
    }

    @PostMapping("/terminate")
    public CommonResponse<TerminateResponseDto> terminate(@RequestBody TerminateVoteRequestDto request) {
        return CommonResponse.success(updateVoteUseCase.terminate(request));
    }

    @PostMapping("/terminate/all")
    public CommonResponse<Void> terminateAll(@RequestBody TerminateVoteRequestDto request) {
        updateVoteUseCase.terminateAll(request);
        return CommonResponse.success();
    }

    @DeleteMapping("/item")
    public CommonResponse<DeleteVoteItemResponseDto> deleteVoteItem(@RequestParam String voteItemId) {
        DeleteVoteItemRequestDto inDto = DeleteVoteItemRequestDto.builder()
                .voteItemId(parseLong(voteItemId))
                .build();

        return CommonResponse.success(deleteVoteItemUseCase.deleteItem(inDto));
    }

    @DeleteMapping("")
    public CommonResponse<DeleteVoteResponseDto> deleteVote(@RequestParam Long voteId) {
        return CommonResponse.success(deleteVoteUseCase.deleteVote(voteId));
    }
}
