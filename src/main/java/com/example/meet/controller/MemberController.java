package com.example.meet.controller;

import static java.lang.Long.parseLong;

import com.example.meet.infrastructure.CommonResponse;
import com.example.meet.infrastructure.dto.request.member.EditMemberPrevillegeRequestDto;
import com.example.meet.infrastructure.dto.request.MemberListRequestDto;
import com.example.meet.infrastructure.dto.request.MemberRequestDto;
import com.example.meet.infrastructure.dto.request.member.EditMemberDepositRequestDto;
import com.example.meet.infrastructure.dto.response.member.MemberDepositResponseDto;
import com.example.meet.infrastructure.dto.response.member.MemberPrevillegeResponseDto;
import com.example.meet.infrastructure.dto.response.member.MemberResponseDto;
import com.example.meet.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("")
    @Tag(name = "Member", description = "회원")
    @Operation(summary = "멤버 조회",
            description = "Authorization header require",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = MemberResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404",
                            description = "존재하지 않는 멤버",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(responseCode = "403",
                            description = "권한이 없음",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )

            })
    public CommonResponse<MemberResponseDto> findMember(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        MemberRequestDto inDto = MemberRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .build();
        return CommonResponse.success(memberService.findMember(inDto));
    }

    @GetMapping("/previllege")
    @Tag(name = "Member", description = "회원")
    @Operation(summary = "멤버 권한 조회",
            description = "Authorization header require",
            responses = {@ApiResponse(responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = MemberPrevillegeResponseDto.class)
                    )
            ),
                    @ApiResponse(responseCode = "404",
                            description = "존재하지 않는 멤버",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )}
    )
    public CommonResponse<MemberPrevillegeResponseDto> searchMemberPrevillege(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return CommonResponse.success(memberService.searchMemberPrevillege(Long.valueOf(userId)));
    }

    @PutMapping("/previllege")
    @Tag(name = "Member", description = "회원")
    @Operation(summary = "멤버 권한 수정",
            description = "Authorization header require",
            responses = {
            @ApiResponse(responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(responseCode = "404",
                    description = "존재하지 않는 멤버",
                    content = @Content(
                            mediaType = "application/json"
                    )
            ),
            @ApiResponse(responseCode = "403",
                    description = "관리자 권한이 없음",
                    content = @Content(
                    mediaType = "application/json"
                    )
            )

    })
    public CommonResponse<Void> editMemberPrevillege(@RequestBody EditMemberPrevillegeRequestDto requestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        EditMemberPrevillegeRequestDto inDto = EditMemberPrevillegeRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .memberId(requestDto.getMemberId())
                .option(requestDto.getOption())
                .uuid(requestDto.getUuid())
                .build();

        memberService.editMemberPrevillege(inDto);
        return CommonResponse.success();
    }

    @GetMapping("/list")
    @Tag(name = "Member", description = "회원")
    @Operation(summary = "멤버 리스트 조회",
            description = "Authorization header require",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = MemberResponseDto.class))
                            )
                    ),
                    @ApiResponse(responseCode = "404",
                            description = "존재하지 않는 멤버",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(responseCode = "403",
                            description = "관리자 권한이 없음",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )

    })
    public CommonResponse<List<MemberResponseDto>> findMemberList(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        MemberListRequestDto inDto = MemberListRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .build();
        return CommonResponse.success(memberService.findMemberList(inDto));
    }

    @PutMapping("/deposit")
    @Tag(name = "Member", description = "회원")
    @Operation(summary = "회비 입금 여부 수정",
            description = "Authorization header require",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = MemberDepositResponseDto.class))
                            )
                    ),
                    @ApiResponse(responseCode = "404",
                            description = "존재하지 않는 멤버",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(responseCode = "403",
                            description = "관리자 권한이 없음",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )

            })
    public CommonResponse<MemberDepositResponseDto> editMemberDeposit(@RequestBody EditMemberDepositRequestDto requestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        EditMemberDepositRequestDto inDto = EditMemberDepositRequestDto.builder()
                .memberId(requestDto.getMemberId())
                .userId(parseLong(userDetails.getUsername()))
                .option(requestDto.getOption())
                .build();
        return CommonResponse.success(memberService.editMemberDeposit(inDto));
    }

}
