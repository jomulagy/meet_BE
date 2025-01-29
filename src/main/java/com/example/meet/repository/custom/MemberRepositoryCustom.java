package com.example.meet.repository.custom;

import com.example.meet.entity.Member;
import java.util.List;

public interface MemberRepositoryCustom {
    List<Member> findMembersWithPrivilegeEndDateOnNextMonth10();
}
