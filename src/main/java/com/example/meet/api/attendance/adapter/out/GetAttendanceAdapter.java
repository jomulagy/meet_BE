package com.example.meet.api.attendance.adapter.out;

import com.example.meet.api.attendance.application.domain.entity.Attendance;
import com.example.meet.api.attendance.application.port.out.GetAttendancePort;
import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.api.post.application.domain.entity.Post;
import com.example.meet.infrastructure.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GetAttendanceAdapter implements GetAttendancePort {
    private final AttendanceRepository attendanceRepository;

    @Override
    public Optional<Attendance> findLatestByMember(Member member) {
        return attendanceRepository.findFirstByMemberOrderByAttendanceDateDesc(member);
    }

    @Override
    public Optional<Attendance> findByMemberAndPost(Member member, Post post) {
        return attendanceRepository.findByMemberAndPost(member, post);
    }

    @Override
    public List<Attendance> findAllByPost(Post post) {
        return attendanceRepository.findByPost(post);
    }

    @Override
    public Optional<Attendance> findLatestByMemberExcluding(Member member, Long excludeId) {
        return attendanceRepository.findFirstByMemberAndIdNotOrderByAttendanceDateDescIdDesc(member, excludeId);
    }
}
