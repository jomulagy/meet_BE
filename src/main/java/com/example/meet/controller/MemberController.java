package com.example.meet.controller;

import com.example.meet.common.CommonResponse;
import com.example.meet.common.dto.response.MemberPrevillegeDto;
import com.example.meet.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/previllege")
    public CommonResponse<MemberPrevillegeDto> searchMemberPrevillege(){
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        return CommonResponse.success(memberService.searchMemberPrevillege(Long.valueOf(userId)));
    }
}
