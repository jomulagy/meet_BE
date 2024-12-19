package com.example.meet.common.dto.response.member;

import com.example.meet.common.enumulation.MemberPrevillege;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberPrevillegeResponseDto {
    @Schema(description = "접근 권한",
            example = "admin"
    )
    private MemberPrevillege previllege;
}
