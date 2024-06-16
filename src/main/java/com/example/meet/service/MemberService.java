package com.example.meet.service;

import com.example.meet.common.dto.request.EditMemberPrevillegeRequestDto;
import com.example.meet.common.dto.request.MemberListRequestDto;
import com.example.meet.common.dto.response.MemberPrevillegeResponseDto;
import com.example.meet.common.dto.response.MemberResponseDto;
import com.example.meet.common.exception.BusinessException;
import com.example.meet.common.enumulation.EditMemberPrevillegeOption;
import com.example.meet.common.enumulation.ErrorCode;
import com.example.meet.common.enumulation.MemberPrevillege;
import com.example.meet.entity.Member;
import com.example.meet.repository.MemberRepository;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public MemberPrevillegeResponseDto searchMemberPrevillege(Long userId) {
        Optional<Member> user = memberRepository.findById(userId);
        if(!user.isPresent()){
            throw new BusinessException(ErrorCode.MEMBER_NOT_EXISTS);
        }
        return new MemberPrevillegeResponseDto(user.get().getPrevillege());

    }

    public List<MemberResponseDto> findMemberList(MemberListRequestDto inDto) {
        //로그인 한 유저 확인
        Member member = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인 (관리자 여부)
        if(!member.getPrevillege().equals(MemberPrevillege.admin)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        List<Member> resultList = memberRepository.findAll();

        return resultList.stream().map(
                m -> {
                    Boolean isFirst = false;

                    if(m.getUuid() == null){
                        isFirst = true;
                    }

                    return MemberResponseDto.builder()
                            .id(m.getId().toString())
                            .name(m.getName())
                            .email(m.getEmail())
                            .previllege(m.getPrevillege().toString())
                            .isFirst(isFirst.toString())
                            .build();
                })
                .collect(Collectors.toList());
    }

    public void editMemberPrevillege(EditMemberPrevillegeRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인 (관리자 여부)
        if(!user.getPrevillege().equals(MemberPrevillege.admin)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        Member member = memberRepository.findById(inDto.getMemberId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //최초 가입시 uuid 추가하기
        if(inDto.getUuid() != null){
            member.updateUUID(inDto.getUuid());
        }

        if(inDto.getOption().equals(EditMemberPrevillegeOption.accept) && member.getUuid() != null){
            member.updatePrevillege(MemberPrevillege.accepted);
        }
        else{
            member.updatePrevillege(MemberPrevillege.denied);
        }
        memberRepository.save(member);

    }
}
