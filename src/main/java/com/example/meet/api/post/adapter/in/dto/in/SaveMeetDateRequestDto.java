package com.example.meet.api.post.adapter.in.dto.in;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveMeetDateRequestDto {
    @Schema(hidden = true)
    private Long postId;

    private LocalDate meetDate;
}
