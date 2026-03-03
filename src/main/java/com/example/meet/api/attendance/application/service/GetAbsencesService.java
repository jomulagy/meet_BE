package com.example.meet.api.attendance.application.service;

import com.example.meet.api.attendance.adapter.in.dto.out.AbsencesResponseDto;
import com.example.meet.api.attendance.application.domain.entity.Attendance;
import com.example.meet.api.attendance.application.port.in.GetAbsencesUseCase;
import com.example.meet.api.attendance.application.port.out.GetAttendancePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAbsencesService implements GetAbsencesUseCase {
    private final GetAttendancePort getAttendancePort;

    @Override
    public AbsencesResponseDto getAbsences() {
        List<Attendance> latestAttendances = getAttendancePort.findLatestAttendanceForAllMembers();

        List<AbsencesResponseDto.AbsenceMemberInfo> memberList = latestAttendances.stream()
                .filter(a -> a.getConsecutiveAbsences() != null && a.getConsecutiveAbsences() >= 1)
                .map(a -> AbsencesResponseDto.AbsenceMemberInfo.builder()
                        .memberId(a.getMember().getId())
                        .name(a.getMember().getName())
                        .consecutiveAbsences(a.getConsecutiveAbsences())
                        .isPenalty(a.getConsecutiveAbsences() >= 3)
                        .build())
                .collect(Collectors.toList());

        return AbsencesResponseDto.builder()
                .totalCount(memberList.size())
                .memberList(memberList)
                .build();
    }
}
