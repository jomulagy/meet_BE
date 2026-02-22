package com.example.meet.api.attendance.adapter.in.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveAttendanceRequestDto {
    @Schema(description = "게시글 ID", example = "89")
    private Long postId;

    @Schema(description = "참석한 회원 ID 리스트", example = "[1, 2, 3]")
    private List<Long> attendedMembers;

    @Schema(description = "불참한 회원 ID 리스트", example = "[4, 5]")
    private List<Long> absentMembers;
}
