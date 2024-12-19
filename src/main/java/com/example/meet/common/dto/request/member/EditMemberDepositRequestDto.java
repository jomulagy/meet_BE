package com.example.meet.common.dto.request.member;

import com.example.meet.common.enumulation.EditMemberPrevillegeOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Builder
public class EditMemberDepositRequestDto {
    @Schema(description = "대상 멤버 id", example = "12345677")
    private final Long memberId;
    @Schema(description = "입금여부", example = "true")
    private String option;
    @Schema(hidden = true)
    private final Long userId;
}
