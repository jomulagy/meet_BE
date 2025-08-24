package com.example.meet.infrastructure.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminAccessTokenResponseDto {
    @Schema(description = "accessToken", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIyOTI3Mzk4OTgzIiwiYXV0aCI6IlVTRVIiLCJleHAiOjE3MTg1NTAzOTl9.gUlLPwEh53Nz5t_77l-4cWfkARk6EIZc2l1OsO6oNdw")
    private String adminAccessToken;
}
