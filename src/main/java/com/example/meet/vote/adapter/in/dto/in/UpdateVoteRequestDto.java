package com.example.meet.vote.adapter.in.dto.in;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateVoteRequestDto {
    private Long voteId;
    private List<Long> voteItemIdList;
}
