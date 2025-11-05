package com.example.meet.member.adapter.out;

import com.example.meet.entity.Member;
import com.example.meet.infrastructure.repository.MemberRepository;
import com.example.meet.member.application.port.out.GetMemberPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class GetMemberAdapter implements GetMemberPort {
    private final MemberRepository memberRepository;

    @Override
    public Optional<Member> getMemberById(Long memberId) {
        return memberRepository.findById(memberId);
    }
}
