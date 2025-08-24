package com.example.meet.infrastructure.dto.response.place;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindPlaceVoteResponseDto {
    private String meetTitle;
    private String endDate;
    private String isAuthor;
}
