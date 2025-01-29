package com.example.meet.repository.custom.impl;

import com.example.meet.entity.QMember;
import com.example.meet.entity.QPrivilege;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.example.meet.entity.Member;
import com.example.meet.repository.custom.MemberRepositoryCustom;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Member> findMembersWithPrivilegeEndDateOnNextMonth10() {
        LocalDateTime nextMonth10 = LocalDateTime.now().plusMonths(1).withDayOfMonth(10);

        QMember member = QMember.member;
        QPrivilege privilege = QPrivilege.privilege;

        return queryFactory.selectFrom(member)
                .join(member.privilege, privilege)
                .where(privilege.endDate.eq(nextMonth10))
                .fetch();
    }

    @Override
    public List<Member> findMembersWithPrivilegeEndDateOnCurMonth10() {
        LocalDateTime month10 = LocalDateTime.now().withDayOfMonth(10);

        QMember member = QMember.member;
        QPrivilege privilege = QPrivilege.privilege;

        return queryFactory.selectFrom(member)
                .join(member.privilege, privilege)
                .where(privilege.endDate.eq(month10))
                .fetch();
    }
}
