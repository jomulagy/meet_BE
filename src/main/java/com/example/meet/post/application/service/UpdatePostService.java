package com.example.meet.post.application.service;

import com.example.meet.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.entity.Member;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.MemberPrevillege;
import com.example.meet.infrastructure.enumulation.VoteType;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.post.adapter.in.dto.in.UpdatePostRequestDto;
import com.example.meet.post.adapter.in.dto.out.UpdatePostResponseDto;
import com.example.meet.post.application.domain.entity.Post;
import com.example.meet.post.application.port.in.GetPostUseCase;
import com.example.meet.post.application.port.in.UpdatePostUseCase;
import com.example.meet.post.application.port.out.UpdatePostPort;
import com.example.meet.vote.application.domain.entity.Vote;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

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
        if(!(user.getPrevillege().equals(MemberPrevillege.admin) || post.getAuthor() == user)){
            throw new BusinessException(ErrorCode.MEET_EDIT_PERMISSION_REQUIRED);
        }

        List<Vote> voteList = post.getVoteList();

        //투표한 필드는 편집 불가
        if(post.getDate() != null && voteList.stream().anyMatch(vote -> vote.getType() == VoteType.DATE)){
            throw new BusinessException(ErrorCode.VOTE_REQUIRED);
        }

        //투표한 필드는 편집 불가(장소)
        if(post.getPlace() != null && voteList.stream().anyMatch(vote -> vote.getType() == VoteType.PLACE)){
            throw new BusinessException(ErrorCode.VOTE_REQUIRED);
        }

        updatePostPort.update(inDto);

        return UpdatePostResponseDto.builder()
                .id(post.getId())
                .build();
    }
}
