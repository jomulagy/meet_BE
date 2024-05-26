package com.example.meet.controller;

import static java.lang.Long.parseLong;

import com.example.meet.common.CommonResponse;
import com.example.meet.common.dto.request.MemberListRequestDto;
import com.example.meet.common.dto.response.MemberPrevillegeResponseDto;
import com.example.meet.common.dto.response.MemberResponseDto;
import com.example.meet.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/previllege")
    public CommonResponse<MemberPrevillegeResponseDto> searchMemberPrevillege(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return CommonResponse.success(memberService.searchMemberPrevillege(Long.valueOf(userId)));
    }

    @GetMapping("/list")
    public CommonResponse<List<MemberResponseDto>> findMemberList(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        MemberListRequestDto inDto = MemberListRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .build();
        return CommonResponse.success(memberService.findMemberList(inDto));
    }

}
