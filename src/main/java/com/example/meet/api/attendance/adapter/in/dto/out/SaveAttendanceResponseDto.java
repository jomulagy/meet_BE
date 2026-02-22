package com.example.meet.api.attendance.adapter.in.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaveAttendanceResponseDto {
    private Long postId;
    private Integer totalAttended;
    private Integer totalAbsent;
    private List<String> penaltyNotifiedMembers;
}
