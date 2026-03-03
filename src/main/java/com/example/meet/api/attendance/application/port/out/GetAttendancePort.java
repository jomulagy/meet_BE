package com.example.meet.api.attendance.application.port.out;

import com.example.meet.api.attendance.application.domain.entity.Attendance;
import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.api.post.application.domain.entity.Post;

import java.util.List;
import java.util.Optional;

public interface GetAttendancePort {
    Optional<Attendance> findLatestByMember(Member member);

    Optional<Attendance> findByMemberAndPost(Member member, Post post);

    List<Attendance> findAllByPost(Post post);

    Optional<Attendance> findLatestByMemberExcluding(Member member, Long excludeId);

    List<Attendance> findLatestAttendanceForAllMembers();
}
