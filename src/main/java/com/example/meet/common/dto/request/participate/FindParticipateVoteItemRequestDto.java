package com.example.meet.common.dto.request.participate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindParticipateVoteItemRequestDto {
    private Long meetId;
    private Long userId;
}
