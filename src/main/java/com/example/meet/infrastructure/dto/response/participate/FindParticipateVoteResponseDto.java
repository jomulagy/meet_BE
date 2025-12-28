package com.example.meet.infrastructure.dto.response.participate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindParticipateVoteResponseDto {
    private Long id;
    private String endDate;
    private List<FindParticipateVoteItemResponseDto> itemList;
    private boolean isActive;
    private boolean isVoted;
    @Builder.Default
    private List<String> participants = new ArrayList<>();
}
