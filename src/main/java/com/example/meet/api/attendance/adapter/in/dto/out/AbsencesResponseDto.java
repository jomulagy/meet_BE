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
public class AbsencesResponseDto {
    private int totalCount;
    private List<AbsenceMemberInfo> memberList;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AbsenceMemberInfo {
        private Long memberId;
        private String name;
        private Integer consecutiveAbsences;
        private Boolean isPenalty;
    }
}
