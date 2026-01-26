package com.example.meet.service;

import com.example.meet.api.member.adapter.in.dto.in.EditMemberPrevillegeRequestDto;
import com.example.meet.api.member.adapter.in.dto.in.MemberListRequestDto;
import com.example.meet.api.member.adapter.in.dto.in.MemberRequestDto;
import com.example.meet.api.member.adapter.in.dto.in.EditMemberDepositRequestDto;
import com.example.meet.infrastructure.dto.response.member.MemberDepositResponseDto;
import com.example.meet.infrastructure.dto.response.member.MemberPrevillegeResponseDto;
import com.example.meet.infrastructure.dto.response.member.MemberResponseDto;
import com.example.meet.infrastructure.enumulation.MemberRole;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.enumulation.EditMemberPrevillegeOption;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.infrastructure.mapper.MemberMapper;
import com.example.meet.infrastructure.repository.MemberRepository;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final MemberMapper memberMapper = MemberMapper.INSTANCE;

    public MemberPrevillegeResponseDto searchMemberPrevillege(Long userId) {
        Optional<Member> user = memberRepository.findById(userId);
        if(!user.isPresent()){
            throw new BusinessException(ErrorCode.MEMBER_NOT_EXISTS);
        }
        return new MemberPrevillegeResponseDto(user.get().getRole());

    }

    public List<MemberResponseDto> findMemberList(MemberListRequestDto inDto) {
        //로그인 한 유저 확인
        Member member = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인 (관리자 여부)
        if(!member.getRole().equals(MemberRole.admin)){
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
                            .deposit(m.getDeposit().toString())
                            .previllege(m.getRole().toString())
                            .isFirst(isFirst.toString())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void editMemberPrevillege(EditMemberPrevillegeRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인 (관리자 여부)
        if(!user.getRole().equals(MemberRole.admin)){
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
            member.updatePrevillege(MemberRole.accepted);
        }
        else{
            member.updatePrevillege(MemberRole.denied);
        }
        memberRepository.save(member);

    }

    public MemberResponseDto findMember(MemberRequestDto inDto) {
        //로그인 한 유저 확인
        Member member = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인
        if(member.getRole().equals(MemberRole.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        return memberMapper.entityToDto(member);
    }

    public MemberDepositResponseDto editMemberDeposit(EditMemberDepositRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인 (관리자 여부)
        if(!user.getRole().equals(MemberRole.admin)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        Member member = memberRepository.findById(inDto.getMemberId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        if(inDto.getOption() == null){
            throw new BusinessException(ErrorCode.VALUE_REQUIRED);
        }

        if("true".equals(inDto.getOption())) {
            member.payed();
        } else {
            member.unPayed();
        }

        memberRepository.save(member);

        return new MemberDepositResponseDto(member.getDeposit().toString());
    }
}
