package com.example.meet.auth.application.port.in;

import com.example.meet.infrastructure.auth.JwtTokenResponseDto;
import com.example.meet.infrastructure.dto.request.KakaoTokenRequestDto;

public interface AuthLoginUseCase {
    JwtTokenResponseDto login(KakaoTokenRequestDto request);
}
