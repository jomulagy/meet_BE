package com.example.meet.vote.application.adapter.in.dto.in;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteScheduleVoteItemRequestDto {
    private Long userId;
    private Long voteItemId;
}
