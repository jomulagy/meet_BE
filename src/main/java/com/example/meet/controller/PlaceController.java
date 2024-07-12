package com.example.meet.controller;

import static java.lang.Long.parseLong;

import com.example.meet.common.CommonResponse;
import com.example.meet.common.dto.request.place.CreatePlaceVoteItemRequestDto;
import com.example.meet.common.dto.request.place.DeletePlaceVoteItemRequestDto;
import com.example.meet.common.dto.request.place.FindPlaceVoteItemRequestDto;
import com.example.meet.common.dto.request.place.UpdatePlaceVoteRequestDto;
import com.example.meet.common.dto.response.place.CreatePlaceVoteItemResponseDto;
import com.example.meet.common.dto.response.place.DeletePlaceVoteItemResponseDto;
import com.example.meet.common.dto.response.place.FindPlaceVoteItemResponseDto;
import com.example.meet.common.dto.request.place.FindPlaceVoteRequestDto;
import com.example.meet.common.dto.response.place.FindPlaceVoteResponseDto;
import com.example.meet.common.dto.response.place.UpdatePlaceVoteResponseDto;
import com.example.meet.service.PlaceService;
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
@RequestMapping("/meet/place")
public class PlaceController {
    private final PlaceService placeService;

    @GetMapping("")
    @Tag(name = "place vote", description = "모임 장소 투표")
    @Operation(summary = "장소 투표 조회",
            description = "Authorization header require<br>type - Routine",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = FindPlaceVoteResponseDto.class)
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
    public CommonResponse<FindPlaceVoteResponseDto> findPlaceVote(@RequestParam String meetId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        FindPlaceVoteRequestDto inDto = FindPlaceVoteRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(parseLong(meetId))
                .build();

        return CommonResponse.success(placeService.findPlaceVote(inDto));
    }

    @GetMapping("/item/list")
    @Tag(name = "place vote", description = "모임 장소 투표")
    @Operation(summary = "장소 투표 리스트 조회",
            description = "Authorization header require<br>type - Routine",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = FindPlaceVoteItemResponseDto.class))
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
    public CommonResponse<List<FindPlaceVoteItemResponseDto>> findPlaceVoteItemList(@RequestParam String meetId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        FindPlaceVoteItemRequestDto inDto = FindPlaceVoteItemRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(parseLong(meetId))
                .build();

        return CommonResponse.success(placeService.findPlaceVoteItemList(inDto));
    }

    @PostMapping("/item")
    @Tag(name = "place vote", description = "모임 장소 투표")
    @Operation(summary = "투표항목 추가",
            description = "Authorization header require<br>type - Routine",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CreatePlaceVoteItemResponseDto.class)
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
    @Parameter(name = "place", description = "장소", example = "강남역")
    public CommonResponse<CreatePlaceVoteItemResponseDto> createPlaceVoteItem(@RequestBody CreatePlaceVoteItemRequestDto request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        CreatePlaceVoteItemRequestDto inDto = CreatePlaceVoteItemRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(request.getMeetId())
                .place(request.getPlace())
                .build();

        return CommonResponse.success(placeService.createPlaceVoteItem(inDto));
    }

    @DeleteMapping("/item")
    @Tag(name = "place vote", description = "모임 장소 투표")
    @Operation(summary = "투표항목 삭제",
            description = "Authorization header require<br>type - Routine",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = DeletePlaceVoteItemResponseDto.class)
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
    @Parameter(name = "placeVoteItemId", description = "투표항목 id", example = "1")
    public CommonResponse<DeletePlaceVoteItemResponseDto> deletePlaceVoteItem(@RequestParam String placeVoteItemId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        DeletePlaceVoteItemRequestDto inDto = DeletePlaceVoteItemRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .placeVoteItemId(parseLong(placeVoteItemId))
                .build();

        return CommonResponse.success(placeService.deletePlaceVoteItem(inDto));
    }

    @PutMapping("")
    @Tag(name = "place vote", description = "모임 장소 투표")
    @Operation(summary = "장소 투표 저장",
            description = "Authorization header require<br>type - Routine",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UpdatePlaceVoteResponseDto.class)
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
    @Parameter(name = "placeVoteItemList", description = "투표한 항목 id 리스트", example = "[1, 2]")
    public CommonResponse<UpdatePlaceVoteResponseDto> updatePlaceVote(@RequestBody UpdatePlaceVoteRequestDto requestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        UpdatePlaceVoteRequestDto inDto = UpdatePlaceVoteRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(requestDto.getMeetId())
                .placeVoteItemList(requestDto.getPlaceVoteItemList())
                .build();

        return CommonResponse.success(placeService.updatePlaceVote(inDto));
    }
}
