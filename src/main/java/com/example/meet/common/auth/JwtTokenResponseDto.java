package com.example.meet.common.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class JwtTokenResponseDto {
    @Schema(description = "토큰 타입", example = "Bearer")
    private String grantType;
    @Schema(description = "accessToken", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyOTI3Mzk4OTgzIiwiYXV0aCI6IlVTRVIiLCJleHAiOjE3MTg1NTAzOTl9.gUlLPwEh53Nz5t_77l-4cWfkARk6EIZc2l1OsO6oNdw")
    private String accessToken;
    @Schema(description = "토큰 타입", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyOTI3Mzk4OTgzIiwiZXhwIjoxNzE4NTUwMzk5fQ.jGHABpyZixCUs4Xc81Of-1513GFU0O1a0QRloiITK-E")
    private String refreshToken;
}
