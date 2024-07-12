package com.example.meet.common.dto.request.place;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeletePlaceVoteItemRequestDto {
    private Long userId;
    private Long placeVoteItemId;
}
