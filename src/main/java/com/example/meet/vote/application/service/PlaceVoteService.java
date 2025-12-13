package com.example.meet.vote.application.service;

import com.example.meet.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.entity.Member;
import com.example.meet.infrastructure.dto.response.member.SimpleMemberResponseDto;
import com.example.meet.infrastructure.dto.request.place.CreatePlaceVoteItemRequestDto;
import com.example.meet.infrastructure.dto.request.place.DeletePlaceVoteItemRequestDto;
import com.example.meet.infrastructure.dto.request.place.FindPlaceVoteItemRequestDto;
import com.example.meet.infrastructure.dto.request.place.UpdatePlaceVoteRequestDto;
import com.example.meet.infrastructure.dto.response.place.CreatePlaceVoteItemResponseDto;
import com.example.meet.infrastructure.dto.response.place.DeletePlaceVoteItemResponseDto;
import com.example.meet.infrastructure.dto.response.place.FindPlaceVoteItemResponseDto;
import com.example.meet.infrastructure.dto.response.place.UpdatePlaceVoteResponseDto;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.MemberPrevillege;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.meet.application.port.in.GetMeetUseCase;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.domain.entity.VoteItem;
import com.example.meet.vote.application.port.in.GetVoteUseCase;
import com.example.meet.vote.application.port.in.PlaceVoteUseCase;
import com.example.meet.vote.application.port.out.CreateVoteItemPort;
import com.example.meet.vote.application.port.out.CreateVotePort;
import com.example.meet.vote.application.port.out.GetVoteItemPort;
import com.example.meet.vote.application.port.out.GetVotePort;
import com.example.meet.vote.application.port.out.UpdateVotePort;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlaceVoteService implements PlaceVoteUseCase {

    private final GetLogginedInfoUseCase getLogginedInfoUseCase;
    private final GetMeetUseCase getMeetUseCase;
    private final GetVoteUseCase getVoteUseCase;
    private final GetVotePort getVotePort;
    private final CreateVotePort createVotePort;
    private final CreateVoteItemPort createVoteItemPort;
    private final GetVoteItemPort getVoteItemPort;
    private final UpdateVotePort updateVotePort;

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public List<FindPlaceVoteItemResponseDto> findPlaceVoteItemList(FindPlaceVoteItemRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();
        if (user.getPrevillege().equals(MemberPrevillege.denied)) {
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }
        Meet meet = getMeetUseCase.get(inDto.getMeetId());
        Vote vote = getOrCreateVote(meet);

        List<FindPlaceVoteItemResponseDto> outDtoList = new ArrayList<>();

        for (VoteItem item : vote.getVoteItems()) {
            String isVote = item.getVoters().contains(user) ? "true" : "false";

            List<SimpleMemberResponseDto> memberList = new ArrayList<>();
            item.getVoters().forEach(member -> memberList.add(
                    SimpleMemberResponseDto.builder()
                            .id(member.getId().toString())
                            .name(member.getName())
                            .build())
            );

            String editable = (item.getAuthor().equals(user) && Boolean.TRUE.equals(item.getEditable()) && item.getVoters().isEmpty())
                    ? "true"
                    : "false";

            outDtoList.add(
                    FindPlaceVoteItemResponseDto.builder()
                            .id(item.getId().toString())
                            .place(item.getContent())
                            .editable(editable)
                            .isVote(isVote)
                            .memberList(memberList)
                            .build()
            );
        }

        return outDtoList;
    }

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public CreatePlaceVoteItemResponseDto createPlaceVoteItem(CreatePlaceVoteItemRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();
        Meet meet = getMeetUseCase.get(inDto.getMeetId());
        Vote vote = getOrCreateVote(meet);

        if (inDto.getPlace() == null || inDto.getPlace().isEmpty()) {
            throw new BusinessException(ErrorCode.PLACE_VALUE_REQUIRED);
        }

        if (meet.getEndDate() != null && meet.getEndDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.PLACE_VOTE_END);
        }

        validateDuplicatePlace(vote, inDto.getPlace());

        VoteItem voteItem = VoteItem.builder()
                .content(inDto.getPlace())
                .editable(Boolean.TRUE)
                .author(user)
                .vote(vote)
                .build();

        VoteItem saved = createVoteItemPort.create(voteItem);

        return CreatePlaceVoteItemResponseDto.builder()
                .id(saved.getId().toString())
                .place(saved.getContent())
                .editable("true")
                .isVote(saved.getVoters().contains(user) ? "true" : "false")
                .memberList(mapMembers(saved))
                .build();
    }

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public DeletePlaceVoteItemResponseDto deletePlaceVoteItem(DeletePlaceVoteItemRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();

        VoteItem voteItem = getVoteItemPort.get(inDto.getPlaceVoteItemId()).orElseThrow(
                () -> new BusinessException(ErrorCode.PLACE_VOTE_ITEM_NOT_EXISTS)
        );

        validateVoteAvailability(voteItem.getVote().getMeet());

        if (!voteItem.getAuthor().equals(user) || !Boolean.TRUE.equals(voteItem.getEditable()) || !voteItem.getVoters().isEmpty()) {
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        getVoteItemPort.delete(voteItem);
        return DeletePlaceVoteItemResponseDto.builder()
                .status("success")
                .build();
    }

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public UpdatePlaceVoteResponseDto updatePlaceVote(UpdatePlaceVoteRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();
        Meet meet = getMeetUseCase.get(inDto.getMeetId());
        Vote vote = getVoteUseCase.getActiveVote(meet).vote();

        for (Long id : inDto.getPlaceVoteItemList()) {
            getVoteItemPort.get(id).orElseThrow(
                    () -> new BusinessException(ErrorCode.PLACE_VOTE_ITEM_NOT_EXISTS)
            );
        }

        updateVotePort.updateVoters(vote.getId(), user, inDto.getPlaceVoteItemList());
        return UpdatePlaceVoteResponseDto.builder()
                .status("success")
                .build();
    }

    private Vote getOrCreateVote(Meet meet) {
        Vote vote = getVotePort.getByMeetId(meet.getId()).orElse(null);
        if (vote == null) {
            vote = createVotePort.create(
                    Vote.builder()
                            .meet(meet)
                            .activeYn(Boolean.TRUE)
                            .endDate(meet.getEndDate())
                            .build()
            );
        }
        return vote;
    }

    private void validateDuplicatePlace(Vote vote, String place) {
        vote.getVoteItems().stream()
                .filter(item -> place.equals(item.getContent()))
                .findAny()
                .ifPresent(item -> {
                    throw new BusinessException(ErrorCode.PLACE_VOTE_ITEM_DUPLICATED);
                });
    }

    private void validateVoteAvailability(Meet meet) {
        Member user = getLogginedInfoUseCase.get();
        if (user.getPrevillege().equals(MemberPrevillege.denied)) {
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        if (meet.getEndDate() != null && meet.getEndDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.PLACE_VOTE_END);
        }
    }

    private List<SimpleMemberResponseDto> mapMembers(VoteItem voteItem) {
        List<SimpleMemberResponseDto> memberList = new ArrayList<>();
        voteItem.getVoters().forEach(member -> {
                    memberList.add(SimpleMemberResponseDto.builder()
                            .id(member.getId().toString())
                            .name(member.getName())
                            .build());
                }
        );
        return memberList;
    }
}
