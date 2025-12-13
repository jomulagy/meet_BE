package com.example.meet.vote.application.adapter.in;

import static java.lang.Long.parseLong;

import com.example.meet.infrastructure.CommonResponse;
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
import com.example.meet.vote.application.port.in.VoteUseCase;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/vote")
public class VoteController {

    private final VoteUseCase voteUseCase;

    @GetMapping("")
    @Tag(name = "vote", description = "모임 투표")
    @Operation(summary = "투표 조회",
            description = "Authorization header require<br>type - Routine",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FindVoteResponseDto.class)
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
    public CommonResponse<FindVoteResponseDto> findVote(@RequestParam(name = "meetId") String meetId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        FindVoteRequestDto inDto = FindVoteRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(parseLong(meetId))
                .build();

        return CommonResponse.success(voteUseCase.findVote(inDto));
    }

    @GetMapping("/item/list")
    @Tag(name = "vote", description = "모임 투표")
    @Operation(summary = "투표 리스트 조회",
            description = "Authorization header require<br>type - Routine",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = FindVoteItemResponseDto.class))
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
    public CommonResponse<List<FindVoteItemResponseDto>> findVoteItemList(@RequestParam(name = "meetId") String meetId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        FindVoteItemRequestDto inDto = FindVoteItemRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(parseLong(meetId))
                .build();

        return CommonResponse.success(voteUseCase.findVoteItems(inDto));
    }

    @PostMapping("/item")
    @Tag(name = "vote", description = "모임 투표")
    @Operation(summary = "투표항목 추가",
            description = "Authorization header require<br>type - Routine",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreateVoteItemResponseDto.class)
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
    public CommonResponse<CreateVoteItemResponseDto> createVoteItem(@RequestBody CreateVoteItemRequestDto request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        CreateVoteItemRequestDto inDto = CreateVoteItemRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(request.getMeetId())
                .content(request.getContent())
                .build();

        return CommonResponse.success(voteUseCase.createVoteItem(inDto));
    }

    @DeleteMapping("/item")
    @Tag(name = "vote", description = "모임 투표")
    @Operation(summary = "투표항목 삭제",
            description = "Authorization header require<br>type - Routine",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DeleteVoteItemResponseDto.class)
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
    @Parameter(name = "voteItemId", description = "투표항목 id", example = "1")
    public CommonResponse<DeleteVoteItemResponseDto> deleteVoteItem(@RequestParam String voteItemId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        DeleteVoteItemRequestDto inDto = DeleteVoteItemRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .voteItemId(parseLong(voteItemId))
                .build();

        return CommonResponse.success(voteUseCase.deleteVoteItem(inDto));
    }

    @PutMapping("")
    @Tag(name = "vote", description = "모임 투표")
    @Operation(summary = "투표 저장",
            description = "Authorization header require<br>type - Routine",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UpdateVoteResponseDto.class)
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
    public CommonResponse<UpdateVoteResponseDto> updateVote(@RequestBody UpdateVoteRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        UpdateVoteRequestDto inDto = UpdateVoteRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(requestDto.getMeetId())
                .voteItemIdList(requestDto.getVoteItemIdList())
                .build();

        return CommonResponse.success(voteUseCase.updateVote(inDto));
    }
}
