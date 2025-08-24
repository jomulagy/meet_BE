package com.example.meet.infrastructure.dto.request.participate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindParticipateVoteRequestDto {
    private Long userId;
    private Long meetId;
}
