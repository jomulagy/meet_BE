package com.example.meet.api.attendance.application.service;

import com.example.meet.api.attendance.adapter.in.dto.out.AttendanceRecordDto;
import com.example.meet.api.attendance.adapter.in.dto.out.GetAttendanceResponseDto;
import com.example.meet.api.attendance.application.domain.entity.Attendance;
import com.example.meet.api.attendance.application.port.in.GetAttendanceUseCase;
import com.example.meet.api.attendance.application.port.out.GetAttendancePort;
import com.example.meet.api.post.application.domain.entity.Post;
import com.example.meet.api.post.application.port.in.GetPostUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAttendanceService implements GetAttendanceUseCase {
    private final GetAttendancePort getAttendancePort;
    private final GetPostUseCase getPostUseCase;

    @Override
    public GetAttendanceResponseDto getAttendanceByPostId(Long postId) {
        // 1. Post 조회
        Post post = getPostUseCase.getEntity(postId);

        // 2. 해당 Post의 모든 출석 기록 조회
        List<Attendance> attendances = getAttendancePort.findAllByPost(post);

        // 3. DTO로 변환
        List<AttendanceRecordDto> attendanceRecords = attendances.stream()
                .map(attendance -> AttendanceRecordDto.builder()
                        .attendanceId(attendance.getId())
                        .memberId(attendance.getMember().getId())
                        .memberName(attendance.getMember().getName())
                        .isAttended(attendance.getIsAttended())
                        .consecutiveAbsences(attendance.getConsecutiveAbsences())
                        .penaltyPaid(attendance.getPenaltyPaid())
                        .attendanceDate(attendance.getAttendanceDate())
                        .build())
                .collect(Collectors.toList());

        // 4. 참석/불참 카운트
        long attendedCount = attendances.stream()
                .filter(Attendance::getIsAttended)
                .count();
        long absentCount = attendances.size() - attendedCount;

        return GetAttendanceResponseDto.builder()
                .postId(post.getId())
                .postTitle(post.getTitle())
                .totalAttended((int) attendedCount)
                .totalAbsent((int) absentCount)
                .attendanceRecords(attendanceRecords)
                .build();
    }
}
