package com.example.meet.api.attendance.adapter.in.dto.out;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttendanceRecordDto {
    private Long attendanceId;
    private Long memberId;
    private String memberName;
    private Boolean isAttended;
    private Integer consecutiveAbsences;
    private LocalDate attendanceDate;
}
