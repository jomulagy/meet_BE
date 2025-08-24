package com.example.meet.infrastructure.dto.request.member;

import com.example.meet.infrastructure.enumulation.EditMemberPrevillegeOption;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class EditMemberPrevillegeRequestDto {
    @Schema(hidden = true)
    private Long userId;
    @Schema(description = "유저 id", example = "2927398983")
    private Long memberId;
    @Schema(description = "권한 추가/삭제 여부", example = "accept")
    private EditMemberPrevillegeOption option;
    @Schema(description = "카카오 uuid", example = "DjgIOQAzAzYFKRosGSwYKR0vAzoOPQg9CEE")
    private String uuid;
}
