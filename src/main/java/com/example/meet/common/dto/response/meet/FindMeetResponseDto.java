package com.example.meet.common.dto.response.meet;

import com.example.meet.common.dto.response.date.FindSimpleDateResponseDto;
import com.example.meet.common.dto.response.place.FindSimplePLaceResponseDto;
import com.example.meet.common.enumulation.MeetType;
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
public class FindMeetResponseDto {
    @Schema(description = "모임 id", example = "1")
    private Long id;
    @Schema(description = "모임 제목", example = "2024 3분기 정기모임")
    private String title;
    @Schema(description = "모임 내용", example = "정기 모임 입니다.")
    private String content;
    @Schema(description = "모임 종류", example = "Routine")
    private MeetType type;
    @Schema(description = "모임 날짜")
    private FindSimpleDateResponseDto date;
    @Schema(description = "모임장소")
    private FindSimplePLaceResponseDto place;
    @Schema(description = "작성자 여부", example = "true")
    private String isAuthor;
    @Schema(description = "참여자 수", example = "2")
    private String participantsNum;
    @Schema(description = "참여자 이름 리스트", example = "['김지훈','장지연']")
    private List<String> participants;
}
