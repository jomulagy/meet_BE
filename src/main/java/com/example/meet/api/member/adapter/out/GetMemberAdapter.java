package com.example.meet.api.member.adapter.out;

import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.infrastructure.repository.MemberRepository;
import com.example.meet.api.member.application.port.out.GetMemberPort;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.example.meet.api.auth.application.domain.entity.QPrivilege.privilege;
import static com.example.meet.api.member.application.domain.entity.QMember.member;

@Repository
@RequiredArgsConstructor
public class GetMemberAdapter implements GetMemberPort {
    private final MemberRepository memberRepository;
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<Member> getMemberById(Long memberId) {
        return memberRepository.findById(memberId);
    }

    @Override
    public List<String> getAllUUIDs() {
        return jpaQueryFactory
                .select(member.uuid)
                .from(member)
                .fetch();
    }
}
