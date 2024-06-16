package com.example.meet.controller;

import static java.lang.Long.parseLong;

import com.example.meet.common.CommonResponse;
import com.example.meet.common.dto.request.CreateMeetRequestDto;
import com.example.meet.common.dto.request.EditMemberPrevillegeRequestDto;
import com.example.meet.common.dto.request.FindMeetRequestDto;
import com.example.meet.common.dto.response.CreateMeetResponseDto;
import com.example.meet.common.dto.response.FindMeetResponseDto;
import com.example.meet.service.MeetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meet")
public class MeetController {
    private final MeetService meetService;

    @PostMapping("")
    @Tag(name = "Meet", description = "모임")
    @Operation(summary = "모임 생성",
            description = "Authorization header require",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateMeetResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404",
                            description = "존재하지 않는 멤버",
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
    public CommonResponse<CreateMeetResponseDto> createMeet(@RequestBody CreateMeetRequestDto requestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        CreateMeetRequestDto inDto = CreateMeetRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .title(requestDto.getTitle())
                .type(requestDto.getType())
                .date(requestDto.getDate())
                .place(requestDto.getPlace())
                .content(requestDto.getContent())
                .build();

        return CommonResponse.success(meetService.createMeet(inDto));
    }

    @GetMapping("")
    @Tag(name = "Meet", description = "모임")
    @Operation(summary = "모임 조회",
            description = "Authorization header require",
            //TODO: update api response
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateMeetRequestDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404",
                            description = "존재하지 않는 멤버",
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
    public CommonResponse<FindMeetResponseDto> findMeet(@RequestParam String meetId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        FindMeetRequestDto inDto = FindMeetRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(parseLong(meetId))
                .build();

        return CommonResponse.success(meetService.findMeet(inDto));
    }
}
