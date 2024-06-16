package com.example.meet.common.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditMeetRequestDto {
    @Schema(hidden = true)
    private Long userId;
    @Schema(hidden = true)
    private Long meetId;
    @Schema(description = "모임 제목", example = "2024 3분기 정기모임")
    private String title;
    @Schema(description = "모임 내용", example = "정기 모임 입니다.")
    private String content;
    @Schema(description = "모임 날짜", example = "2024-07-05")
    private String date;
    @Schema(description = "모임장소", example = "강남역")
    private String place;

}
