package com.example.meet.post.adapter.in;

import static java.lang.Long.parseLong;

import com.example.meet.infrastructure.CommonResponse;
import com.example.meet.post.adapter.in.dto.CreateMeetRequestDto;
import com.example.meet.infrastructure.dto.request.DeleteMeetRequestDto;
import com.example.meet.post.adapter.in.dto.in.GetPostRequestDto;
import com.example.meet.post.adapter.in.dto.in.UpdatePostRequestDto;
import com.example.meet.post.adapter.in.dto.CreateMeetResponseDto;
import com.example.meet.post.adapter.in.dto.out.UpdatePostResponseDto;
import com.example.meet.post.adapter.in.dto.out.GetPostResponseDto;
import com.example.meet.post.application.port.in.CreatePostUseCase;
import com.example.meet.post.application.port.in.GetPostUseCase;
import com.example.meet.post.application.port.in.UpdatePostUseCase;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/post")
public class PostController {
    private final MeetService meetService;
    private final CreatePostUseCase createPostUseCase;
    private final GetPostUseCase getPostUseCase;
    private final UpdatePostUseCase updatePostUseCase;

    @PostMapping("")
    public CommonResponse<CreateMeetResponseDto> createMeet(@RequestBody CreateMeetRequestDto requestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        CreateMeetRequestDto inDto = CreateMeetRequestDto.builder()
                .title(requestDto.getTitle())
                .date(requestDto.getDate())
                .time(requestDto.getTime())
                .place(requestDto.getPlace())
                .content(requestDto.getContent())
                .voteDeadline(requestDto.getVoteDeadline())
                .participationDeadline(requestDto.getParticipationDeadline())
                .build();

        return CommonResponse.success(createPostUseCase.createMeet(inDto));
    }

    @PostMapping("/create/notification")
    public CommonResponse<CreateMeetResponseDto> createNotification(@RequestBody CreateMeetRequestDto requestDto){
        return CommonResponse.success(createPostUseCase.createNotification(requestDto));
    }

    @GetMapping("/list")
    public CommonResponse<List<GetPostResponseDto>> findPostList(){
        return CommonResponse.success(getPostUseCase.findPostList());
    }

    @GetMapping("/{postId}")
    public CommonResponse<GetPostResponseDto> findPost(@PathVariable(name = "postId") Long postId){
        GetPostRequestDto inDto = GetPostRequestDto.builder()
                .postId(postId)
                .build();

        return CommonResponse.success(getPostUseCase.get(inDto));
    }

    @PutMapping("")
    public CommonResponse<UpdatePostResponseDto> editMeet(@RequestParam String meetId, @RequestBody UpdatePostRequestDto requestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        UpdatePostRequestDto inDto = UpdatePostRequestDto.builder()
                .postId(parseLong(meetId))
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .date(requestDto.getDate())
                .time(requestDto.getTime())
                .place(requestDto.getPlace())
                .build();

        return CommonResponse.success(updatePostUseCase.update(inDto));
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
