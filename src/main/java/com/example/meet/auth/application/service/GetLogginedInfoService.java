package com.example.meet.auth.application.service;

import com.example.meet.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.entity.Member;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.member.application.port.out.GetMemberPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetLogginedInfoService implements GetLogginedInfoUseCase {
    private final GetMemberPort getMemberPort;

    @Override
    public Member get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        try {
            Long userId = Long.parseLong(authentication.getName());
            return getMemberPort.getMemberById(userId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS));
        } catch (NumberFormatException ex) {
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }
    }
}
