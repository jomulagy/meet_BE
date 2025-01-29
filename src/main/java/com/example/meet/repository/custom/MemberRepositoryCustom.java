package com.example.meet.repository.custom;

import com.example.meet.entity.Member;
import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> findMembersWithPrivilegeEndDateOnNextMonth10();
    List<Member> findMembersWithPrivilegeEndDateOnCurMonth10();
    List<Member> findMembersWithDepositIsDepositFalse();

}
