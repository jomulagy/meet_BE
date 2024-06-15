package com.example.meet.common.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class MemberResponseDto {
    @Schema(description = "유저 id", example = "2927398983")
    private String id;
    @Schema(description = "유저 이름", example = "김지훈")
    private String name;
    @Schema(description = "접근 권한", example = "admin")
    private String previllege;
    @Schema(description = "카카오 email id", example = "kjh980309@naver.com")
    private String email;
    @Schema(description = "첫 가입 여부", example = "true")
    private String isFirst;
}
