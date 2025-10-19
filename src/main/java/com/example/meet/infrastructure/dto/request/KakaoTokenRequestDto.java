package com.example.meet.infrastructure.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoTokenRequestDto {
    @Schema(description = "kakao access Token", example = "Bearer bBnK9-rsqIr7K4W5vU1ITuOsFoTciGK6AAAAAQo9dVwAAAGPm3-zvaL4plhSrbcM", required = true)
    private String accessToken;

    private String refreshToken;
}
