package com.example.meet.member.application.port.out;

import com.example.meet.entity.Member;

import java.util.Optional;

public interface GetMemberPort {
    Optional<Member> getMemberById(Long memberId);
}
