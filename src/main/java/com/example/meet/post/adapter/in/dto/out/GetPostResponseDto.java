package com.example.meet.post.adapter.in.dto.out;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetPostResponseDto {
    @Schema(description = "모임 id", example = "1")
    private Long id;
    @Schema(description = "모임 제목", example = "2024 3분기 정기모임")
    private String title;
    @Schema(description = "모임 내용", example = "정기 모임 입니다.")
    private String content;
    @Schema(description = "작성자 여부", example = "true")
    private boolean isAuthor;
    private boolean isVoteClosed;
    private String type;
}
