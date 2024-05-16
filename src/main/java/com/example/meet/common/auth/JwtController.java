package com.example.meet.common.auth;

import com.example.meet.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/token")
public class JwtController {
    private final JwtService jwtService;

    @PostMapping("/refresh")
    public CommonResponse<JwtTokenResponseDto> refreshAccessToken(@RequestBody JwtRefreshTokenRequestDto in) {
        return CommonResponse.success(jwtService.refreshAccessToken(in));
    }
}
