package com.example.meet.common.auth;

import com.example.meet.common.exception.BusinessException;
import com.example.meet.common.variables.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public JwtTokenResponseDto refreshAccessToken(JwtRefreshTokenRequestDto in){
        String refreshToken = in.getRefreshToken();
        if (refreshToken == null || !jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new BusinessException(ErrorCode.JWT_TOKEN_INVALID);
        }
        UsernamePasswordAuthenticationToken authenticationToken = jwtTokenProvider.getAuthenticationFromRefreshToken(refreshToken);
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        JwtTokenResponseDto jwtToken = jwtTokenProvider.generateToken(authentication);
        return jwtToken;
    }
}
