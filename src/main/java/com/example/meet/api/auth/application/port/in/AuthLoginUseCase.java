package com.example.meet.api.auth.application.port.in;

import com.example.meet.infrastructure.auth.JwtTokenResponseDto;
import com.example.meet.api.auth.adapter.in.dto.in.KakaoTokenRequestDto;

public interface AuthLoginUseCase {
    JwtTokenResponseDto login(KakaoTokenRequestDto request);
}
