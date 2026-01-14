package com.example.meet.api.post.application.service;

import com.example.meet.api.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.MemberRole;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.api.post.adapter.in.dto.in.UpdatePostRequestDto;
import com.example.meet.api.post.adapter.in.dto.out.UpdatePostResponseDto;
import com.example.meet.api.post.application.domain.entity.Post;
import com.example.meet.api.post.application.port.in.GetPostUseCase;
import com.example.meet.api.post.application.port.in.UpdatePostUseCase;
import com.example.meet.api.post.application.port.out.UpdatePostPort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class UpdatePostService implements UpdatePostUseCase {
    private final GetLogginedInfoUseCase getLogginedInfoUseCase;
    private final GetPostUseCase getPostUseCase;

    private final UpdatePostPort updatePostPort;

    DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public UpdatePostResponseDto update(UpdatePostRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();

        //모임 조회
        Post post = getPostUseCase.getEntity(inDto.getPostId());

        //작성자 or 관리자 인지 확인
        if(!(user.getRole().equals(MemberRole.admin) || post.getAuthor() == user)){
            throw new BusinessException(ErrorCode.MEET_EDIT_PERMISSION_REQUIRED);
        }

        updatePostPort.update(inDto);

        return UpdatePostResponseDto.builder()
                .id(post.getId())
                .build();
    }

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAdminAccess(authentication)")
    public void terminateVote(Long id) {
        updatePostPort.terminateVote(id);
    }
}
