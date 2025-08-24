package com.example.meet.infrastructure.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class JwtRefreshTokenRequestDto {
    private String refreshToken;
}
