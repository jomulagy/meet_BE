package com.example.meet.infrastructure.dto.request;

import com.example.meet.infrastructure.enumulation.MeetType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"userId", "type", "title", "date", "time"})
@Builder
public class CreateMeetRequestDto {
    @Schema(hidden = true)
    private Long userId;

    @Schema(description = "제목", example = "3분기 정기 일정")
    private String title;

    @Schema(description = "모임 종류(사용자가 생성할 경우 CUSTOM)", example = "CUSTOM")
    private MeetType type;

    @Schema(description = "날짜", example = "2024-07-05")
    private String date;

    @Schema(description = "시간", example = "19:00")
    private String time;

    private String place;

    @Schema(description = "내용", example = "내용", required = false)
    private String content;
}
