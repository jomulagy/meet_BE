package com.example.meet.infrastructure.security;

import com.example.meet.entity.Member;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.MemberPrevillege;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.member.application.port.out.GetMemberPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("memberPermissionEvaluator")
@RequiredArgsConstructor
public class MemberPermissionEvaluator {
    private final GetMemberPort getMemberPort;

    public boolean hasAccess(Long memberId) {
        Member member = getMemberPort.getMemberById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS));

        if (MemberPrevillege.denied.equals(member.getPrevillege())) {
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        return true;
    }
}
