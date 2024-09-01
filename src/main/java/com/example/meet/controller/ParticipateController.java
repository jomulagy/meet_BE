package com.example.meet.controller;

import static java.lang.Long.parseLong;

import com.example.meet.common.CommonResponse;
import com.example.meet.common.dto.request.participate.UpdateParticipateVoteRequestDto;
import com.example.meet.common.dto.request.participate.FindParticipateVoteItemRequestDto;
import com.example.meet.common.dto.request.participate.FindParticipateVoteRequestDto;
import com.example.meet.common.dto.response.participate.FindParticipateVoteItemResponseDto;
import com.example.meet.common.dto.response.participate.FindParticipateVoteResponseDto;
import com.example.meet.common.dto.response.participate.UpdateParticipateVoteResponseDto;
import com.example.meet.service.ParticipateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/meet/participate")
public class ParticipateController {
    private final ParticipateService participateService;

    @GetMapping("")
    @Tag(name = "participate vote", description = "참여여부 투표")
    @Operation(summary = "참여여부 투표 조회",
            description = "Authorization header require<br>type - Routine",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FindParticipateVoteResponseDto.class)
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
    public CommonResponse<FindParticipateVoteResponseDto> findParticipateVote(@RequestParam String meetId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        FindParticipateVoteRequestDto inDto = FindParticipateVoteRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(parseLong(meetId))
                .build();

        return CommonResponse.success(participateService.findParticipateVote(inDto));
    }

    @GetMapping("/item/list")
    @Tag(name = "participate vote", description = "참여여부 투표")
    @Operation(summary = "참여여부 투표 항목 리스트",
            description = "Authorization header require<br>type - Routine",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = FindParticipateVoteItemResponseDto.class))
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
    public CommonResponse<List<FindParticipateVoteItemResponseDto>> findParticipateVoteItemList(@RequestParam String meetId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        FindParticipateVoteItemRequestDto inDto = FindParticipateVoteItemRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(parseLong(meetId))
                .build();

        return CommonResponse.success(participateService.findParticipateVoteItemList(inDto));
    }

    @PutMapping("")
    @Tag(name = "participate vote", description = "참여여부 투표")
    @Operation(summary = "참여여부 투표 저장",
            description = "Authorization header require<br>type - Routine",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UpdateParticipateVoteResponseDto.class)
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
    @Parameter(name = "participateVoteItemIdList", description = "투표한 항목 id 리스트", example = "[1, 2]")
    public CommonResponse<UpdateParticipateVoteResponseDto> updateParticipateVote(@RequestBody UpdateParticipateVoteRequestDto requestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        UpdateParticipateVoteRequestDto inDto = UpdateParticipateVoteRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(requestDto.getMeetId())
                .participateVoteItemIdList(requestDto.getParticipateVoteItemIdList())
                .build();

        return CommonResponse.success(participateService.updateParticipateVote(inDto));
    }
}
