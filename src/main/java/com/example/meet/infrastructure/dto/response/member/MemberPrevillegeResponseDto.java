package com.example.meet.infrastructure.dto.response.member;

import com.example.meet.infrastructure.enumulation.MemberRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberPrevillegeResponseDto {
    @Schema(description = "접근 권한",
            example = "admin"
    )
    private MemberRole previllege;
}
