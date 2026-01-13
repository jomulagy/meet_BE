package com.example.meet.infrastructure.repository;

import com.example.meet.infrastructure.enumulation.MemberPrevillege;
import com.example.meet.api.member.application.domain.entity.Member;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    List<Member> findByPrevillegeGreaterThanAndUuidIsNotNull(MemberPrevillege previllege);

    List<Member> findByDeposit(Boolean b);
}
