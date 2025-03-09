package com.example.meet.controller;

import static java.lang.Long.parseLong;

import com.example.meet.common.CommonResponse;
import com.example.meet.common.dto.request.CreateMeetRequestDto;
import com.example.meet.common.dto.request.DeleteMeetRequestDto;
import com.example.meet.common.dto.request.EditMeetRequestDto;
import com.example.meet.common.dto.request.FindMeetRequestDto;
import com.example.meet.common.dto.response.meet.CreateMeetResponseDto;
import com.example.meet.common.dto.response.meet.EditMeetResponseDto;
import com.example.meet.common.dto.response.meet.FindMeetResponseDto;
import com.example.meet.common.dto.response.meet.FindMeetSimpleResponseDto;
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
                .time(requestDto.getTime())
                .place(requestDto.getPlace())
                .content(requestDto.getContent())
                .build();

        return CommonResponse.success(meetService.createMeet(inDto));
    }

    @GetMapping("/list")
    @Tag(name = "Meet", description = "모임")
    @Operation(summary = "모임 전체 조회",
            description = "Authorization header require<br>type - Routine",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FindMeetSimpleResponseDto.class)
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
    public CommonResponse<List<FindMeetSimpleResponseDto>> findMeetList(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        FindMeetRequestDto inDto = FindMeetRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .build();

        return CommonResponse.success(meetService.findMeetList(inDto));
    }

    @GetMapping("")
    @Tag(name = "Meet", description = "모임")
    @Operation(summary = "모임 조회",
            description = "Authorization header require<br>type - Routine",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FindMeetResponseDto.class)
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
    public CommonResponse<FindMeetResponseDto> findMeet(@RequestParam String meetId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        FindMeetRequestDto inDto = FindMeetRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(parseLong(meetId))
                .build();

        return CommonResponse.success(meetService.findMeet(inDto));
    }

    @PutMapping("")
    @Tag(name = "Meet", description = "모임")
    @Operation(summary = "모임 수정",
            description = "Authorization header require<br> 투표료 결정된 필드는 수정 할 수 없습니다.<br>변경되지 않은 필드도 전송합니다.",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EditMeetResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404",
                            description = "존재하지 않는 멤버, 존재하지 않는 모임",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(responseCode = "403",
                            description = "멤버 권한이 없음, 관리자와 작성자만 편집 할 수 있음",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(responseCode = "400",
                            description = "투표한 필드는 편집 할 수 없음",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )

            })
    @Parameter(name = "meetId", description = "모임 id", example = "1")
    public CommonResponse<EditMeetResponseDto> editMeet(@RequestParam String meetId, @RequestBody EditMeetRequestDto requestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        EditMeetRequestDto inDto = EditMeetRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(parseLong(meetId))
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .date(requestDto.getDate())
                .time(requestDto.getTime())
                .place(requestDto.getPlace())
                .build();

        return CommonResponse.success(meetService.editMeet(inDto));
    }

    @DeleteMapping("")
    @Tag(name = "Meet", description = "모임")
    @Operation(summary = "모임 삭제",
            description = "Authorization header require",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공"
                    ),
                    @ApiResponse(responseCode = "404",
                            description = "존재하지 않는 멤버, 존재하지 않는 모임",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(responseCode = "403",
                            description = "멤버 권한이 없음, 관리자와 작성자만 삭제 할 수 있음",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )
            })
    @Parameter(name = "meetId", description = "모임 id", example = "1")
    public CommonResponse<Void> deleteMeet(@RequestParam String meetId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        DeleteMeetRequestDto inDto = DeleteMeetRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(parseLong(meetId))
                .build();
        meetService.deleteMeet(inDto);
        return CommonResponse.success();
    }


}
