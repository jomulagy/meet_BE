package com.example.meet.api.vote.application.service;

import com.example.meet.api.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.MemberRole;
import com.example.meet.infrastructure.enumulation.VoteType;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.utils.DateTimeUtils;
import com.example.meet.api.post.application.domain.entity.Post;
import com.example.meet.api.vote.adapter.in.dto.in.FindVoteRequestDto;
import com.example.meet.api.vote.adapter.in.dto.out.FindVoteItemResponseDto;
import com.example.meet.api.vote.adapter.in.dto.out.FindVoteResponseDto;
import com.example.meet.api.vote.application.domain.entity.Vote;
import com.example.meet.api.vote.application.domain.entity.VoteItem;
import com.example.meet.api.vote.application.port.in.GetVoteUseCase;
import com.example.meet.api.vote.application.port.out.GetVotePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GetVoteService implements GetVoteUseCase {
    private final GetVotePort getVotePort;
    private final GetLogginedInfoUseCase getLogginedInfoUseCase;

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public List<FindVoteResponseDto> getFindVoteResponseDtoList(FindVoteRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();

        List<FindVoteResponseDto> responseDtoList = new ArrayList<>();
        List<FindVoteItemResponseDto> voteItemResponseDtoList;
        List<Vote> voteList = getVotePort.getByPostId(inDto.getPostId());

        for(Vote vote : voteList) {
            boolean isVoteAfter = false;
            String endDate = vote.getEndDate() != null ? DateTimeUtils.formatWithOffset(vote.getEndDate()) : null;

            voteItemResponseDtoList = new ArrayList<>();

            for (VoteItem voteItem : vote.getVoteItems()) {
                boolean isVoted = false;
                List<Member> voters = voteItem.getVoters() == null ? List.of() : voteItem.getVoters();

                if (!isVoteAfter && voters.contains(user)) {
                    isVoteAfter = true;
                }

                if(vote.getActiveYn()) {
                   isVoted = voters.contains(user);
                } else {
                    isVoted = voteItem.equals(vote.getResult());
                }

                voteItemResponseDtoList.add(
                        FindVoteItemResponseDto.builder()
                                .id(voteItem.getId())
                                .value(voteItem.getContent())
                                .isVoted(isVoted)
                                .editable(voteItem.getAuthor().equals(user) || user.getRole().equals(MemberRole.admin))
                                .voterList(voters.stream().map(Member::getName).toList())
                                .build()
                );
            }

            responseDtoList.add(
                    FindVoteResponseDto.builder()
                            .id(vote.getId())
                            .title(vote.getTitle())
                            .endDate(endDate)
                            .isDuplicate(vote.isDuplicate())
                            .isActive(vote.getActiveYn())
                            .isVoted(isVoteAfter)
                            .type(VoteType.of(vote.getType()))
                            .itemList(voteItemResponseDtoList)
                            .build()
            );
        }

        return responseDtoList;
    }

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public FindVoteResponseDto getResponseDto(Long voteId) {
        Member user = getLogginedInfoUseCase.get();
        Optional<Vote> voteOptional = getVotePort.get(voteId);
        List<FindVoteItemResponseDto> voteItemResponseDtoList = new ArrayList<>();

        if(voteOptional.isEmpty()) throw new BusinessException(ErrorCode.VOTE_NOT_EXISTS);

        Vote vote = voteOptional.get();
        boolean isVoteAfter = false;
        String endDate = vote.getEndDate() != null ? DateTimeUtils.formatWithOffset(vote.getEndDate()) : null;

        voteItemResponseDtoList = new ArrayList<>();

        if(vote.getActiveYn()) {
            for (VoteItem voteItem : vote.getVoteItems()) {
                boolean isVoted = false;
                List<Member> voters = voteItem.getVoters() == null ? List.of() : voteItem.getVoters();

                if (!isVoteAfter && voters.contains(user)) {
                    isVoteAfter = true;
                }

                if(vote.getActiveYn()) {
                    isVoted = voters.contains(user);
                } else {
                    isVoted = voteItem.equals(vote.getResult());
                }

                voteItemResponseDtoList.add(
                        FindVoteItemResponseDto.builder()
                                .id(voteItem.getId())
                                .value(voteItem.getContent())
                                .isVoted(isVoted)
                                .editable(voteItem.getAuthor().equals(user) || user.getRole().equals(MemberRole.admin))
                                .voterList(voters.stream().map(Member::getName).toList())
                                .build()
                );
            }
        }

        return FindVoteResponseDto.builder()
                .id(vote.getId())
                .title(vote.getTitle())
                .endDate(endDate)
                .isDuplicate(vote.isDuplicate())
                .isActive(vote.getActiveYn())
                .isVoted(isVoteAfter)
                .type(VoteType.of(vote.getType()))
                .itemList(voteItemResponseDtoList)
                .build();
    }

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public List<Vote> getVoteListByPost(Post post) {
        return getVotePort.getByPostId(post.getId());
    }

    @Override
    @PreAuthorize("@memberPermissionEvaluator.hasAccess(authentication)")
    public Vote getVote(Long voteId) {
        return getVotePort.get(voteId).orElseThrow(() -> new BusinessException(ErrorCode.MEET_NOT_EXISTS));
    }
}
