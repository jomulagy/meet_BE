package com.example.meet.post.adapter.in.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteMeetRequestDto {
    @Schema(hidden = true)
    private Long userId;
    @Schema(hidden = true)
    private Long meetId;
}
