package com.example.meet.api.participate.adapter.in.dto.in;

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
    private Long postId;
}
