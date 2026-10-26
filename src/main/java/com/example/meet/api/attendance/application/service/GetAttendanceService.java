package com.example.meet.api.attendance.application.service;

import com.example.meet.api.attendance.adapter.in.dto.out.AttendanceRecordDto;
import com.example.meet.api.attendance.adapter.in.dto.out.GetAttendanceResponseDto;
import com.example.meet.api.attendance.application.domain.entity.Attendance;
import com.example.meet.api.attendance.application.port.in.GetAttendanceUseCase;
import com.example.meet.api.attendance.application.port.out.GetAttendancePort;
import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.api.member.application.port.out.GetMemberPort;
import com.example.meet.api.post.application.domain.entity.Post;
import com.example.meet.api.post.application.port.in.GetPostUseCase;
import com.example.meet.infrastructure.enumulation.MemberRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetAttendanceService implements GetAttendanceUseCase {
    private final GetAttendancePort getAttendancePort;
    private final GetPostUseCase getPostUseCase;
    private final GetMemberPort getMemberPort;

    @Override
    public GetAttendanceResponseDto getAttendanceByPostId(Long postId) {
        // 1. Post 조회
        Post post = getPostUseCase.getEntity(postId);

        // 2. 해당 Post의 모든 출석 기록 조회
        List<Attendance> attendances = getAttendancePort.findAllByPost(post);

        // 3. 승인된 회원 전체를 응답에 포함하고, 아직 출석 기록이 없는 회원은 입력값을 null로 반환
        Map<Long, Attendance> attendanceByMemberId = new HashMap<>();
        for (Attendance attendance : attendances) {
            attendanceByMemberId.put(attendance.getMember().getId(), attendance);
        }

        List<AttendanceRecordDto> attendanceRecords = new ArrayList<>();
        Set<Long> includedMemberIds = new HashSet<>();
        for (Member member : getMemberPort.getAllMembers()) {
            if (MemberRole.denied.equals(member.getRole())) {
                continue;
            }

            Attendance attendance = attendanceByMemberId.get(member.getId());
            attendanceRecords.add(toRecordDto(member, attendance));
            includedMemberIds.add(member.getId());
        }

        // 권한이 변경된 회원의 기존 출석 기록도 조회 결과에서 빠지지 않도록 유지
        for (Attendance attendance : attendances) {
            Long memberId = attendance.getMember().getId();
            if (includedMemberIds.add(memberId)) {
                attendanceRecords.add(toRecordDto(attendance.getMember(), attendance));
            }
        }

        // 4. 참석/불참 카운트
        long attendedCount = attendances.stream()
                .filter(attendance -> Boolean.TRUE.equals(attendance.getIsAttended()))
                .count();
        long absentCount = attendances.stream()
                .filter(attendance -> Boolean.FALSE.equals(attendance.getIsAttended()))
                .count();

        return GetAttendanceResponseDto.builder()
                .postId(post.getId())
                .postTitle(post.getTitle())
                .totalAttended((int) attendedCount)
                .totalAbsent((int) absentCount)
                .attendanceRecords(attendanceRecords)
                .build();
    }

    private AttendanceRecordDto toRecordDto(Member member, Attendance attendance) {
        return AttendanceRecordDto.builder()
                .attendanceId(attendance == null ? null : attendance.getId())
                .memberId(member.getId())
                .memberName(member.getName())
                .isAttended(attendance == null ? null : attendance.getIsAttended())
                .consecutiveAbsences(attendance == null ? null : attendance.getConsecutiveAbsences())
                .attendanceDate(attendance == null ? null : attendance.getAttendanceDate())
                .build();
    }
}
