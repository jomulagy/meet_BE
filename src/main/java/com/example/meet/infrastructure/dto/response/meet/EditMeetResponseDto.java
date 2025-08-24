package com.example.meet.infrastructure.dto.response.meet;

import com.example.meet.infrastructure.enumulation.MeetType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EditMeetResponseDto {
    @Schema(description = "모임 id", example = "1")
    private Long id;
    @Schema(description = "모임 제목", example = "2024 3분기 정기모임")
    private String title;
    @Schema(description = "모임 내용", example = "정기 모임 입니다.")
    private String content;
    @Schema(description = "모임 종류", example = "Routine")
    private MeetType type;
    @Schema(description = "모임 날짜", example = "2024-07-05")
    private String date;
    @Schema(description = "모임장소", example = "강남역")
    private String place;
    @Schema(description = "참여자 수", example = "2")
    private String participantsNum;
    @Schema(description = "참여자 이름 리스트", example = "['김지훈','장지연']")
    private List<String> participants;
}
