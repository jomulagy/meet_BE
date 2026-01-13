package com.example.meet.auth.adapter.in;

import static java.lang.Long.parseLong;

import com.example.meet.auth.application.port.in.AuthLoginUseCase;
import com.example.meet.infrastructure.CommonResponse;
import com.example.meet.auth.adapter.in.dto.in.KakaoTokenRequestDto;
import com.example.meet.infrastructure.auth.JwtTokenResponseDto;
import com.example.meet.infrastructure.dto.response.AdminAccessTokenResponseDto;
import com.example.meet.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {
    private final AuthLoginUseCase authLoginUseCase;
    private final AuthService authService;

    @PostMapping("/login")
    @Tag(name = "Auth", description = "인증")
    @Operation(summary = "로그인",
            responses = {@ApiResponse(responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JwtTokenResponseDto.class)
                    )
            ),
                    @ApiResponse(responseCode = "403",
                            description = "권한 없음",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )}
    )
    public CommonResponse<JwtTokenResponseDto> login(@RequestBody KakaoTokenRequestDto request) {
        JwtTokenResponseDto jwtTokenResponseDto = authLoginUseCase.login(request);
        return CommonResponse.success(jwtTokenResponseDto);
    }

    @GetMapping("/admin/accessToken")
    @Tag(name = "Auth", description = "인증")
    @Operation(summary = "관리자 accessToken 조회",
            description = "Authorization header require",
            responses = {
                    @ApiResponse(responseCode = "200",
                            description = "성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JwtTokenResponseDto.class)
                            )
                    ),
                    @ApiResponse(responseCode = "403",
                            description = "권한 없음",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    ),
                    @ApiResponse(responseCode = "400",
                            description = "잘못된 accessToken",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )}
    )
    public CommonResponse<AdminAccessTokenResponseDto> findAdminAccessToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return CommonResponse.success(authService.findAdminAccessToken(parseLong(userDetails.getUsername())));
    }
}
