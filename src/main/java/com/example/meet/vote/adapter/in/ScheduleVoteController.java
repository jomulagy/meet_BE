package com.example.meet.vote.adapter.in;

import com.example.meet.infrastructure.CommonResponse;
import com.example.meet.vote.adapter.in.dto.in.*;
import com.example.meet.vote.adapter.in.dto.out.*;
import com.example.meet.vote.application.port.in.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.lang.Long.parseLong;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleVoteController {

    private final GetVoteUseCase getVoteUseCase;
    private final GetVoteItemUseCase getVoteItemUseCase;
    private final CreateVoteItemUseCase createVoteItemUseCase;
    private final DeleteVoteItemUseCase deleteVoteItemUseCase;
    private final UpdateVoteUseCase updateVoteUseCase;

    @GetMapping("")
    public CommonResponse<FindVoteResponseDto> findVote(@RequestParam(name = "meetId") String meetId) {
        FindVoteRequestDto inDto = FindVoteRequestDto.builder()
                .meetId(parseLong(meetId))
                .build();

        return CommonResponse.success(getVoteUseCase.get(inDto));
    }

    @GetMapping("/item/list")
    public CommonResponse<List<FindVoteItemResponseDto>> findVoteItemList(@RequestParam(name = "meetId") String meetId) {
        FindVoteItemRequestDto inDto = FindVoteItemRequestDto.builder()
                .meetId(parseLong(meetId))
                .build();

        return CommonResponse.success(getVoteItemUseCase.getItemList(inDto));
    }

    @PostMapping("/item")
    public CommonResponse<CreateVoteItemResponseDto> createVoteItem(@RequestBody CreateVoteItemRequestDto request) {
        CreateVoteItemRequestDto inDto = CreateVoteItemRequestDto.builder()
                .meetId(request.getMeetId())
                .content(request.getContent())
                .build();

        return CommonResponse.success(createVoteItemUseCase.createItem(inDto));
    }

    @DeleteMapping("/item")
    public CommonResponse<DeleteVoteItemResponseDto> deleteVoteItem(@RequestParam String voteItemId) {
        DeleteVoteItemRequestDto inDto = DeleteVoteItemRequestDto.builder()
                .voteItemId(parseLong(voteItemId))
                .build();

        return CommonResponse.success(deleteVoteItemUseCase.deleteItem(inDto));
    }

    @PutMapping("")
    public CommonResponse<UpdateVoteResponseDto> vote(@RequestBody UpdateVoteRequestDto requestDto) {
        UpdateVoteRequestDto inDto = UpdateVoteRequestDto.builder()
                .meetId(requestDto.getMeetId())
                .voteItems(requestDto.getVoteItems())
                .build();

        return CommonResponse.success(updateVoteUseCase.vote(inDto));
    }
}
