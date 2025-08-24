package com.example.meet.infrastructure.auth;

import com.example.meet.infrastructure.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @Tag(name = "Auth", description = "인증")
    @Operation(summary = "JWT 토큰 재발급",
            responses = {@ApiResponse(responseCode = "200",
                    description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = JwtTokenResponseDto.class)
                    )
            ),
                    @ApiResponse(responseCode = "401",
                            description = "accessToken이 잘못됨",
                            content = @Content(
                                    mediaType = "application/json"
                            )
                    )}
    )
    public CommonResponse<JwtTokenResponseDto> refreshAccessToken(@RequestBody JwtRefreshTokenRequestDto in) {
        return CommonResponse.success(jwtService.refreshAccessToken(in));
    }
}
