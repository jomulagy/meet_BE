package com.example.meet.vote.application.service;

import com.example.meet.infrastructure.dto.response.member.SimpleMemberResponseDto;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.MemberPrevillege;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.repository.MeetRepository;
import com.example.meet.infrastructure.repository.MemberRepository;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.entity.Member;
import com.example.meet.vote.application.adapter.in.dto.in.CreateScheduleVoteItemRequestDto;
import com.example.meet.vote.application.adapter.in.dto.in.DeleteScheduleVoteItemRequestDto;
import com.example.meet.vote.application.adapter.in.dto.in.FindScheduleVoteItemRequestDto;
import com.example.meet.vote.application.adapter.in.dto.in.FindScheduleVoteRequestDto;
import com.example.meet.vote.application.adapter.in.dto.in.UpdateScheduleVoteRequestDto;
import com.example.meet.vote.application.adapter.in.dto.out.CreateScheduleVoteItemResponseDto;
import com.example.meet.vote.application.adapter.in.dto.out.DeleteScheduleVoteItemResponseDto;
import com.example.meet.vote.application.adapter.in.dto.out.FindScheduleVoteItemResponseDto;
import com.example.meet.vote.application.adapter.in.dto.out.FindScheduleVoteResponseDto;
import com.example.meet.vote.application.adapter.in.dto.out.UpdateScheduleVoteResponseDto;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.domain.entity.VoteItem;
import com.example.meet.vote.application.port.in.ScheduleVoteUseCase;
import com.example.meet.vote.application.port.out.VoteRepositoryPort;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ScheduleVoteService implements ScheduleVoteUseCase {

    private final MemberRepository memberRepository;
    private final MeetRepository meetRepository;
    private final VoteRepositoryPort voteRepositoryPort;

    @Override
    public FindScheduleVoteResponseDto findScheduleVote(FindScheduleVoteRequestDto inDto) {
        Member user = getAvailableUser(inDto.getUserId());
        Meet meet = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        String endDate = voteRepositoryPort.findByMeetId(meet.getId())
                .map(Vote::getEndDate)
                .map(com.example.meet.infrastructure.utils.DateTimeUtils::formatWithOffset)
                .orElse(null);

        Boolean isAuthor = meet.getAuthor().equals(user);

        return FindScheduleVoteResponseDto.builder()
                .meetTitle(meet.getTitle())
                .endDate(endDate)
                .isAuthor(isAuthor.toString())
                .build();
    }

    @Override
    public List<FindScheduleVoteItemResponseDto> findScheduleVoteItemList(FindScheduleVoteItemRequestDto inDto) {
        Member user = getAvailableUser(inDto.getUserId());
        Meet meet = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        Vote vote = voteRepositoryPort.findByMeetId(meet.getId()).orElse(null);
        if (vote == null) {
            return new ArrayList<>();
        }

        List<FindScheduleVoteItemResponseDto> outDtoList = new ArrayList<>();

        for (VoteItem item : vote.getVoteItems()) {
            if (item.getDate() == null) {
                continue;
            }
            String isVote = item.getVoters().contains(user) ? "true" : "false";

            List<SimpleMemberResponseDto> memberList = new ArrayList<>();
            item.getVoters().forEach(member ->
                    memberList.add(SimpleMemberResponseDto.builder()
                            .id(member.getId().toString())
                            .name(member.getName())
                            .build())
            );

            String editable = "false";
            if (item.getAuthor() == user && Boolean.TRUE.equals(item.getEditable()) && item.getVoters().isEmpty()) {
                editable = "true";
            }

            outDtoList.add(
                    FindScheduleVoteItemResponseDto.builder()
                            .id(item.getId().toString())
                            .date(item.getDate().toLocalDate().toString())
                            .time(item.getDate().toLocalTime().toString())
                            .editable(editable)
                            .isVote(isVote)
                            .memberList(memberList)
                            .build()
            );
        }

        return outDtoList;
    }

    @Override
    @Transactional
    public CreateScheduleVoteItemResponseDto createScheduleVoteItem(CreateScheduleVoteItemRequestDto inDto) {
        Member user = getAvailableUser(inDto.getUserId());
        Meet meet = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        if (meet.getEndDate() != null && meet.getEndDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.SCHEDULE_VOTE_END);
        }

        Vote vote = voteRepositoryPort.findByMeetId(meet.getId())
                .orElseGet(() -> voteRepositoryPort.saveVote(
                        Vote.builder()
                                .endDate(meet.getEndDate())
                                .activeYn(Boolean.TRUE)
                                .meet(meet)
                                .build()
                ));

        for (VoteItem item : vote.getVoteItems()) {
            if (item.getDate() != null && item.getDate().toLocalDate().equals(inDto.getDate()) &&
                    item.getDate().toLocalTime().equals(inDto.getTime())) {
                throw new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_DUPLICATED);
            }
        }

        VoteItem entity = VoteItem.builder()
                .date(LocalDateTime.of(inDto.getDate(), inDto.getTime()))
                .editable(true)
                .author(user)
                .vote(vote)
                .isParticipate(Boolean.FALSE)
                .build();

        VoteItem voteItem = voteRepositoryPort.saveVoteItem(entity);
        vote.getVoteItems().add(voteItem);
        voteRepositoryPort.saveVote(vote);

        String isVote = voteItem.getVoters().contains(user) ? "true" : "false";

        List<SimpleMemberResponseDto> memberList = new ArrayList<>();
        voteItem.getVoters().forEach(member ->
                memberList.add(SimpleMemberResponseDto.builder()
                        .id(member.getId().toString())
                        .name(member.getName())
                        .build())
        );

        return CreateScheduleVoteItemResponseDto.builder()
                .id(voteItem.getId().toString())
                .date(voteItem.getDate().toLocalDate().toString())
                .time(voteItem.getDate().toLocalTime().toString())
                .editable("true")
                .isVote(isVote)
                .memberList(memberList)
                .build();
    }

    @Override
    @Transactional
    public DeleteScheduleVoteItemResponseDto deleteScheduleVoteItem(DeleteScheduleVoteItemRequestDto inDto) {
        Member user = getAvailableUser(inDto.getUserId());
        VoteItem voteItem = voteRepositoryPort.findVoteItemById(inDto.getVoteItemId()).orElseThrow(
                () -> new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_NOT_EXISTS)
        );

        Meet meet = voteItem.getVote().getMeet();
        if (meet.getEndDate() != null && meet.getEndDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.SCHEDULE_VOTE_END);
        }

        if (voteItem.getVote() != null) {
            voteItem.getVote().getVoteItems().remove(voteItem);
        }

        voteRepositoryPort.deleteVoteItem(voteItem);
        return DeleteScheduleVoteItemResponseDto.builder()
                .id(voteItem.getId().toString())
                .build();
    }

    @Override
    @Transactional
    public UpdateScheduleVoteResponseDto updateScheduleVote(UpdateScheduleVoteRequestDto inDto) {
        Member user = getAvailableUser(inDto.getUserId());
        Meet meet = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        Vote vote = voteRepositoryPort.findByMeetId(meet.getId()).orElseThrow(
                () -> new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_NOT_EXISTS)
        );

        if (vote.getEndDate() != null && vote.getEndDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.SCHEDULE_VOTE_END);
        }

        List<VoteItem> selectedItems = voteRepositoryPort.findVoteItemsById(inDto.getVoteItemIdList());
        for (Long id : inDto.getVoteItemIdList()) {
            boolean exists = selectedItems.stream().anyMatch(item -> item.getId().equals(id));
            if (!exists) {
                throw new BusinessException(ErrorCode.SCHEDULE_VOTE_ITEM_NOT_EXISTS);
            }
        }

        for (VoteItem item : vote.getVoteItems()) {
            List<Long> memberIds = item.getVoters().stream()
                    .map(Member::getId)
                    .toList();

            if (memberIds.contains(user.getId()) && !inDto.getVoteItemIdList().contains(item.getId())) {
                item.getVoters().removeIf(member -> member.equals(user));
            } else if (!memberIds.contains(user.getId()) && inDto.getVoteItemIdList().contains(item.getId())) {
                item.getVoters().add(user);
            }

            voteRepositoryPort.saveVoteItem(item);
        }

        vote.setDateResult();
        voteRepositoryPort.saveVote(vote);

        return UpdateScheduleVoteResponseDto.builder()
                .meetId(meet.getId().toString())
                .build();
    }

    private Member getAvailableUser(Long userId) {
        Member user = memberRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        if (user.getPrevillege().equals(MemberPrevillege.denied)) {
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }
        return user;
    }
}
