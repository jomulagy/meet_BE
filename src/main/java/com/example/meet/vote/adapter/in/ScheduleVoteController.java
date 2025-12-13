package com.example.meet.vote.adapter.in;

import com.example.meet.infrastructure.CommonResponse;
import com.example.meet.vote.adapter.in.dto.in.*;
import com.example.meet.vote.adapter.in.dto.out.*;
import com.example.meet.vote.application.port.in.ScheduleVoteUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.lang.Long.parseLong;

@RestController
@RequiredArgsConstructor
@RequestMapping("/schedule")
public class ScheduleVoteController {

    private final ScheduleVoteUseCase scheduleVoteUseCase;

    @GetMapping("")
    public CommonResponse<FindVoteResponseDto> findVote(@RequestParam(name = "meetId") String meetId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        FindVoteRequestDto inDto = FindVoteRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(parseLong(meetId))
                .build();

        return CommonResponse.success(scheduleVoteUseCase.get(inDto));
    }

    @GetMapping("/item/list")
    public CommonResponse<List<FindVoteItemResponseDto>> findVoteItemList(@RequestParam(name = "meetId") String meetId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        FindVoteItemRequestDto inDto = FindVoteItemRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(parseLong(meetId))
                .build();

        return CommonResponse.success(scheduleVoteUseCase.getItemList(inDto));
    }

    @PostMapping("/item")
    public CommonResponse<CreateVoteItemResponseDto> createVoteItem(@RequestBody CreateVoteItemRequestDto request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        CreateVoteItemRequestDto inDto = CreateVoteItemRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(request.getMeetId())
                .content(request.getContent())
                .build();

        return CommonResponse.success(scheduleVoteUseCase.createItem(inDto));
    }

    @DeleteMapping("/item")
    public CommonResponse<DeleteVoteItemResponseDto> deleteVoteItem(@RequestParam String voteItemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        DeleteVoteItemRequestDto inDto = DeleteVoteItemRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .voteItemId(parseLong(voteItemId))
                .build();

        return CommonResponse.success(scheduleVoteUseCase.deleteItem(inDto));
    }

    @PutMapping("")
    public CommonResponse<UpdateVoteResponseDto> updateVote(@RequestBody UpdateVoteRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        UpdateVoteRequestDto inDto = UpdateVoteRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(requestDto.getMeetId())
                .voteItemIdList(requestDto.getVoteItemIdList())
                .build();

        return CommonResponse.success(scheduleVoteUseCase.update(inDto));
    }
}
