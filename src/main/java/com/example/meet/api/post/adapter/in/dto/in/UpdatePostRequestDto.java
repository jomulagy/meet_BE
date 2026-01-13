package com.example.meet.api.post.adapter.in.dto.in;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdatePostRequestDto {
    @Schema(hidden = true)
    private Long postId;
    @Schema(description = "모임 제목", example = "2024 3분기 정기모임")
    private String title;
    @Schema(description = "모임 내용", example = "정기 모임 입니다.")
    private String content;
    @Schema(description = "모임 날짜", example = "2024-07-05")
    private LocalDate date;
    @Schema(description = "모임 시간", example = "19:00")
    private LocalTime time;
    @Schema(description = "모임장소", example = "강남역")
    private String place;


}
