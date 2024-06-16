package com.example.meet.common.dto.request;

import com.example.meet.common.enumulation.MeetType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateMeetRequestDto {
    @Schema(hidden = true)
    private Long userId;

    @Schema(description = "제목", example = "3분기 정기 일정")
    private String title;

    @Schema(description = "모임 종류", example = "Routine")
    private MeetType type;

    @Schema(description = "날짜", example = "2024-07-05")
    private String date;

    @Schema(description = "장소", example = "강남역")
    private String place;

    @Schema(description = "내용", example = "내용", required = false)
    private String content;
}
