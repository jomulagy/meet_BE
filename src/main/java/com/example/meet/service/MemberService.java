package com.example.meet.service;

import com.example.meet.common.dto.response.MemberPrevillegeDto;
import com.example.meet.common.exception.BusinessException;
import com.example.meet.common.variables.ErrorCode;
import com.example.meet.common.variables.MemberPrevillege;
import com.example.meet.entity.Member;
import com.example.meet.repository.MemberRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberPrevillegeDto searchMemberPrevillege(Long userId) {
        Optional<Member> user = memberRepository.findById(userId);
        if(!user.isPresent()){
            throw new BusinessException(ErrorCode.MEMBER_NOT_EXISTS);
        }
        System.out.println("user.get().getPrevillege() = " + user.get().getPrevillege());
        return new MemberPrevillegeDto(user.get().getPrevillege());

    }
}
