package com.example.meet.vote.application.adapter.in.dto.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateScheduleVoteRequestDto {
    private Long userId;
    private Long meetId;
    private List<Long> voteItemIdList;
}
