package com.example.meet.member.application.port.out;

import com.example.meet.member.application.domain.entity.Member;

import java.util.Optional;

public interface GetMemberPort {
    Optional<Member> getMemberById(Long memberId);
}
