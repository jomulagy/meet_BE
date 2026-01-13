package com.example.meet.auth.application.port.in;

import com.example.meet.infrastructure.auth.JwtTokenResponseDto;
import com.example.meet.auth.adapter.in.dto.in.KakaoTokenRequestDto;

public interface AuthLoginUseCase {
    JwtTokenResponseDto login(KakaoTokenRequestDto request);
}
