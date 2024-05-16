package com.example.meet.controller;

import com.example.meet.common.CommonResponse;
import com.example.meet.common.dto.request.KakaoTokenRequestDto;
import com.example.meet.common.auth.JwtTokenResponseDto;
import com.example.meet.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public CommonResponse<JwtTokenResponseDto> login(@RequestBody KakaoTokenRequestDto request){
        JwtTokenResponseDto jwtTokenResponseDto = authService.login(request);
        return CommonResponse.success(jwtTokenResponseDto);
    }
}
