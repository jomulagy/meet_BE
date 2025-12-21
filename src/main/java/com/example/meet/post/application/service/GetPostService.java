package com.example.meet.post.application.service;

import com.example.meet.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.entity.Member;
import com.example.meet.infrastructure.enumulation.ErrorCode;
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

import java.time.format.DateTimeFormatter;
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

        String date = null;
        String time = null;

        if(post.getDate() != null){
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            date = post.getDate().toLocalDate().format(dateFormatter);
            time = post.getDate().toLocalTime().format(timeFormatter);
        }

        List<Vote> voteList = post.getVoteList();
        Boolean editable = false;

        if(post.getAuthor().equals(user) && voteList.stream().noneMatch(vote -> VoteType.DATE.equals(vote.getType()))){
            editable = true;
        }

        dateResponseDto = GetDateResponseDto.builder()
                .value(date)
                .time(time)
                .editable(editable.toString())
                .build();

        if(post.getAuthor().equals(user) && voteList.stream().noneMatch(vote -> VoteType.PLACE.equals(vote.getType()))){
            editable = true;
        }

        placeResponseDto = GetPlaceResponseDto.builder()
                .value(post.getPlace())
                .editable(editable.toString())
                .build();

        Long participantsNum = post.getParticipantsNum();
        if(post.getParticipantsNum() == null){
            participantsNum = 0L;
        }

        List<String> participants = new ArrayList<>();

        for(Member m : post.getParticipants()){
            participants.add(m.getName());
        }
        return GetPostResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .date(dateResponseDto)
                .place(placeResponseDto)
                .participantsNum(String.valueOf(participantsNum))
                .participants(participants)
                .isAuthor(String.valueOf(post.getAuthor().equals(user)))
                .build();
    }

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public List<GetPostResponseDto> findPostList() {
        String date;
        List<GetPostResponseDto> responseDtoList = new ArrayList<>();

        //모임 조회
        List<Post> postList = getPostPort.findAll();

        for(Post post : postList) {
            date = null;

            if(post.getDate() != null){
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                date = post.getDate().format(dateTimeFormatter);
            }

            GetDateResponseDto dateResponseDto = GetDateResponseDto.builder()
                    .value(date)
                    .build();

            GetPlaceResponseDto placeResponseDto = GetPlaceResponseDto.builder()
                    .value(post.getPlace())
                    .build();

            responseDtoList.add(
                    GetPostResponseDto.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .date(dateResponseDto)
                    .place(placeResponseDto)
                    .build()
            );
        }


        return responseDtoList;
    }

    @Override
    public Post getEntity(Long meetId) {
        return getPostPort.getMeetById(meetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEET_NOT_EXISTS));
    }
}
