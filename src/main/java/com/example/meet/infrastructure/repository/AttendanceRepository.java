package com.example.meet.infrastructure.repository;

import com.example.meet.api.attendance.application.domain.entity.Attendance;
import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.api.post.application.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findFirstByMemberOrderByAttendanceDateDesc(Member member);

    Optional<Attendance> findByMemberAndPost(Member member, Post post);

    List<Attendance> findByPost(Post post);

    // 특정 기록을 제외한 가장 최근 기록 조회 (날짜 및 ID 기준)
    Optional<Attendance> findFirstByMemberAndIdNotOrderByAttendanceDateDescIdDesc(Member member, Long excludeId);

}
