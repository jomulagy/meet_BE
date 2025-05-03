package com.example.meet.controller;

import static java.lang.Long.parseLong;

import com.example.meet.common.CommonResponse;
import com.example.meet.common.dto.response.meet.VoteResponseDto;
import com.example.meet.common.dto.request.CreateScheduleVoteItemRequestDto;
import com.example.meet.common.dto.request.DeleteScheduleVoteItemRequestDto;
import com.example.meet.common.dto.request.FindScheduleVoteItemRequestDto;
import com.example.meet.common.dto.request.FindScheduleVoteRequestDto;
import com.example.meet.common.dto.request.VoteRequestDto;
import com.example.meet.common.dto.response.CreateScheduleVoteItemResponseDto;
import com.example.meet.common.dto.response.DeleteScheduleVoteItemResponseDto;
import com.example.meet.common.dto.response.FindScheduleVoteItemResponseDto;
import com.example.meet.common.dto.response.FindScheduleVoteResponseDto;
import com.example.meet.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meet/schedule")
public class ScheduleController {
    private final ScheduleService scheduleService;

    @GetMapping("")
    @Tag(name = "schedule vote", description = "모임 일정 투표")
    @Operation(summary = "날짜 투표 조회",
            description = "Authorization header require<br>type - Routine",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FindScheduleVoteResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404",
                            description = "존재하지 않는 멤버, 존재하지 않는 모임",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(responseCode = "403",
                            description = "멤버 권한이 없음",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )
            })
    @Parameter(name = "meetId", description = "모임 id", example = "1")
    public CommonResponse<FindScheduleVoteResponseDto> findScheduleVote(@RequestParam String meetId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        FindScheduleVoteRequestDto inDto = FindScheduleVoteRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(parseLong(meetId))
                .build();

        return CommonResponse.success(scheduleService.findScheduleVote(inDto));
    }

    @GetMapping("/item/list")
    @Tag(name = "schedule vote", description = "모임 일정 투표")
    @Operation(summary = "날짜 투표 리스트 조회",
            description = "Authorization header require<br>type - Routine",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = FindScheduleVoteItemResponseDto.class))
                            )
                    ),
                    @ApiResponse(responseCode = "404",
                            description = "존재하지 않는 멤버, 존재하지 않는 모임",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(responseCode = "403",
                            description = "멤버 권한이 없음",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )
            })
    @Parameter(name = "meetId", description = "모임 id", example = "1")
    public CommonResponse<List<FindScheduleVoteItemResponseDto>> findScheduleVoteItemList(@RequestParam String meetId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        FindScheduleVoteItemRequestDto inDto = FindScheduleVoteItemRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(parseLong(meetId))
                .build();

        return CommonResponse.success(scheduleService.findScheduleVoteItemList(inDto));
    }

    @PostMapping("/item")
    @Tag(name = "schedule vote", description = "모임 일정 투표")
    @Operation(summary = "투표항목 추가",
            description = "Authorization header require<br>type - Routine",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateScheduleVoteItemResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404",
                            description = "존재하지 않는 멤버, 존재하지 않는 모임",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(responseCode = "403",
                            description = "멤버 권한이 없음",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )
            })
    @Parameter(name = "meetId", description = "모임 id", example = "1")
    @Parameter(name = "date", description = "날짜", example = "2024-06-27")
    public CommonResponse<CreateScheduleVoteItemResponseDto> createScheduleVoteItem(@RequestBody CreateScheduleVoteItemRequestDto request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        CreateScheduleVoteItemRequestDto inDto = CreateScheduleVoteItemRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(request.getMeetId())
                .date(request.getDate())
                .time(request.getTime())
                .build();

        return CommonResponse.success(scheduleService.createScheduleVoteItem(inDto));
    }

    @DeleteMapping("/item")
    @Tag(name = "schedule vote", description = "모임 일정 투표")
    @Operation(summary = "투표항목 삭제",
            description = "Authorization header require<br>type - Routine",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DeleteScheduleVoteItemResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404",
                            description = "존재하지 않는 멤버, 존재하지 않는 모임",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(responseCode = "403",
                            description = "멤버 권한이 없음",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )
            })
    @Parameter(name = "scheduleVoteItemId", description = "투표항목 id", example = "1")
    public CommonResponse<DeleteScheduleVoteItemResponseDto> deleteScheduleVoteItem(@RequestParam String scheduleVoteItemId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        DeleteScheduleVoteItemRequestDto inDto = DeleteScheduleVoteItemRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .scheduleVoteItemId(parseLong(scheduleVoteItemId))
                .build();

        return CommonResponse.success(scheduleService.deleteScheduleVoteItem(inDto));
    }
}
