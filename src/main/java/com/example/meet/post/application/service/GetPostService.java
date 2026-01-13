package com.example.meet.post.application.service;

import com.example.meet.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.member.application.domain.entity.Member;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.PostType;
import com.example.meet.infrastructure.enumulation.VoteStatus;
import com.example.meet.infrastructure.enumulation.VoteType;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.post.adapter.in.dto.in.GetPostRequestDto;
import com.example.meet.post.adapter.in.dto.out.GetDateResponseDto;
import com.example.meet.post.adapter.in.dto.out.GetPlaceResponseDto;
import com.example.meet.post.adapter.in.dto.out.GetPostResponseDto;
import com.example.meet.post.application.domain.entity.Post;
import com.example.meet.post.application.port.in.GetPostUseCase;
import com.example.meet.post.application.port.out.GetPostPort;
import com.example.meet.vote.application.domain.entity.Vote;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPostService implements GetPostUseCase {
    private final GetLogginedInfoUseCase getLogginedInfoUseCase;
    private final GetPostPort getPostPort;

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public GetPostResponseDto get(GetPostRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();

        //모임 조회
        Post post = getEntity(inDto.getPostId());

        GetDateResponseDto dateResponseDto;
        GetPlaceResponseDto placeResponseDto = null;

        List<Vote> voteList = post.getVoteList();
        Boolean editable = false;

        if(post.getAuthor().equals(user) && voteList.stream().noneMatch(vote -> VoteType.DATE.equals(vote.getType()))){
            editable = true;
        }

        if(post.getAuthor().equals(user) && voteList.stream().noneMatch(vote -> VoteType.PLACE.equals(vote.getType()))){
            editable = true;
        }

        Long participantsNum = post.getParticipantsNum();
        if(post.getParticipantsNum() == null){
            participantsNum = 0L;
        }

        return GetPostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .isAuthor(post.getAuthor().equals(user))
                .isVoteClosed(post.getStatus() != VoteStatus.VOTE)
                .build();
    }

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public List<GetPostResponseDto> findPostList(String type) {
        String date;
        List<GetPostResponseDto> responseDtoList = new ArrayList<>();
        //모임 조회
        List<Post> postList = getPostPort.findListByType(PostType.fromName(type));

        for(Post post : postList) {
            responseDtoList.add(
                    GetPostResponseDto.builder()
                            .id(post.getId())
                            .title(post.getTitle())
                            .type(post.getType().getName())
                            .build()
            );
        }

        return responseDtoList;
    }

    @Override
    public Post getEntity(Long meetId) {
        return getPostPort.getPostById(meetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEET_NOT_EXISTS));
    }
}
