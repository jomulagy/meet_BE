package com.example.meet.vote.application.service;

import com.example.meet.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.entity.Member;
import com.example.meet.infrastructure.dto.response.member.SimpleMemberResponseDto;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.meet.application.port.in.GetMeetUseCase;
import com.example.meet.vote.adapter.in.dto.in.CreateVoteItemRequestDto;
import com.example.meet.vote.adapter.in.dto.out.CreateVoteItemResponseDto;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.domain.entity.VoteItem;
import com.example.meet.vote.application.port.in.CreateVoteItemUseCase;
import com.example.meet.vote.application.port.in.GetVoteUseCase;
import com.example.meet.vote.application.port.out.CreateVoteItemPort;
import com.example.meet.vote.application.port.out.CreateVotePort;
import com.example.meet.vote.application.port.out.GetVoteItemPort;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateVoteItemService implements CreateVoteItemUseCase {

    private final GetLogginedInfoUseCase getLogginedInfoUseCase;
    private final GetMeetUseCase getMeetUseCase;
    private final GetVoteUseCase getVoteUseCase;
    private final CreateVotePort createVotePort;
    private final GetVoteItemPort getVoteItemPort;
    private final CreateVoteItemPort createVoteItemPort;

    @Override
    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public CreateVoteItemResponseDto createItem(CreateVoteItemRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();
        Meet meet = getMeetUseCase.get(inDto.getMeetId());
        Vote vote = getVoteUseCase.getActiveVote(meet).vote();

        VoteItem voteItem = buildVoteItem(inDto, vote, user);

        VoteItem saved = createVoteItemPort.create(voteItem);

        List<SimpleMemberResponseDto> memberList = new ArrayList<>();
        saved.getVoters().forEach(member -> memberList.add(
                SimpleMemberResponseDto.builder()
                        .id(member.getId().toString())
                        .name(member.getName())
                        .build()
        ));

        return CreateVoteItemResponseDto.builder()
                .id(saved.getId().toString())
                .date(saved.getDateTime() != null ? saved.getDateTime().toLocalDate().toString() : null)
                .time(saved.getDateTime() != null ? saved.getDateTime().toLocalTime().toString() : null)
                .content(saved.getContent())
                .editable("true")
                .isVote("true")
                .memberList(memberList)
                .build();
    }

    private VoteItem buildVoteItem(CreateVoteItemRequestDto inDto, Vote vote, Member user) {
        // request를 dateTime으로 변환
        DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

        LocalDateTime targetDateTime;

        if (inDto.getDate() == null) {
            targetDateTime = null;
        } else {
            LocalDate date = LocalDate.parse(inDto.getDate(), DATE_FMT);
            LocalTime time = (inDto.getTime() != null)
                    ? LocalTime.parse(inDto.getTime(), TIME_FMT)
                    : LocalTime.MIDNIGHT;

            targetDateTime = LocalDateTime.of(date, time);
        }

        if (targetDateTime != null) {
            vote.getVoteItems().stream()
                    .filter(item -> targetDateTime.equals(item.getDateTime()))
                    .findAny()
                    .ifPresent(item -> {
                        throw new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_DUPLICATED);
                    });
        }

        if (inDto.getContent() != null) {
            vote.getVoteItems().stream()
                    .filter(item -> inDto.getContent().equals(item.getContent()))
                    .findAny()
                    .ifPresent(item -> {
                        throw new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_DUPLICATED);
                    });
        }

        return VoteItem.builder()
                .author(user)
                .content(inDto.getContent())
                .dateTime(targetDateTime)
                .editable(Boolean.TRUE)
                .vote(vote)
                .build();
    }
}
