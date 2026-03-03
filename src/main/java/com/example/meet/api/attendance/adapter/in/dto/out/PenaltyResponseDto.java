package com.example.meet.api.attendance.adapter.in.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PenaltyResponseDto {
    private List<PenaltyMemberInfo> members;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PenaltyMemberInfo {
        private Long memberId;
        private String memberName;
        private Integer consecutiveAbsences;
        private Boolean penaltyPaid;
        private LocalDate paymentDeadline;
    }
}
