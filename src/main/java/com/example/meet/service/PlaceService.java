package com.example.meet.service;


import com.example.meet.infrastructure.dto.request.place.CreatePlaceVoteItemRequestDto;
import com.example.meet.infrastructure.dto.request.place.DeletePlaceVoteItemRequestDto;
import com.example.meet.infrastructure.dto.request.place.FindPlaceVoteItemRequestDto;
import com.example.meet.infrastructure.dto.request.place.FindPlaceVoteRequestDto;
import com.example.meet.infrastructure.dto.request.place.UpdatePlaceVoteRequestDto;
import com.example.meet.infrastructure.dto.response.member.SimpleMemberResponseDto;
import com.example.meet.infrastructure.dto.response.place.CreatePlaceVoteItemResponseDto;
import com.example.meet.infrastructure.dto.response.place.DeletePlaceVoteItemResponseDto;
import com.example.meet.infrastructure.dto.response.place.FindPlaceVoteItemResponseDto;
import com.example.meet.infrastructure.dto.response.place.FindPlaceVoteResponseDto;
import com.example.meet.infrastructure.dto.response.place.UpdatePlaceVoteResponseDto;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.MemberPrevillege;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.utils.DateTimeUtils;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.entity.Member;

import com.example.meet.entity.PlaceVoteItem;
import com.example.meet.infrastructure.mapper.PlaceVoteItemMapper;
import com.example.meet.infrastructure.repository.MeetRepository;
import com.example.meet.infrastructure.repository.MemberRepository;
import com.example.meet.infrastructure.repository.PlaceVoteItemRepository;
import com.example.meet.infrastructure.repository.PlaceVoteRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceService {
    private final MemberRepository memberRepository;
    private final MeetRepository meetRepository;
    private final PlaceVoteRepository placeVoteRepository;
    private final PlaceVoteItemRepository placeVoteItemRepository;

    private final PlaceVoteItemMapper placeVoteItemMapper = PlaceVoteItemMapper.INSTANCE;

    public List<FindPlaceVoteItemResponseDto> findPlaceVoteItemList(FindPlaceVoteItemRequestDto inDto) {

        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        Meet meet = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        List<FindPlaceVoteItemResponseDto> outDtoList = new ArrayList<>();

        if(meet.getPlaceVote() != null && meet.getPlaceVote().getPlaceVoteItems() != null) {
            List<PlaceVoteItem> placeVoteItemList = meet.getPlaceVote().getPlaceVoteItems();
            for (PlaceVoteItem item : placeVoteItemList) {
                String isVote = "false";
                if (item.getPlaceVoters().contains(user)) {
                    isVote = "true";
                }
                List<SimpleMemberResponseDto> memberList = new ArrayList<>();
                item.getPlaceVoters().forEach(member -> {
                            memberList.add(SimpleMemberResponseDto.builder()
                                    .id(member.getId().toString())
                                    .name(member.getName())
                                    .build());
                        }
                );

                //수정 가능 여부 확인
                String editable = "false";
                if (item.getAuthor().equals(user) && item.getEditable() && item.getPlaceVoters().isEmpty()) {
                    editable = "true";
                }
                outDtoList.add(
                        FindPlaceVoteItemResponseDto.builder()
                                .id(item.getId().toString())
                                .place(item.getPlace())
                                .editable(editable)
                                .isVote(isVote)
                                .memberList(memberList)
                                .build()
                );

            }
        }

        return outDtoList;
    }

    public CreatePlaceVoteItemResponseDto createPlaceVoteItem(CreatePlaceVoteItemRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        if(inDto.getPlace() == null || inDto.getPlace().isEmpty()){
            throw new BusinessException(ErrorCode.PLACE_VALUE_REQUIRED);
        }
        Meet meet = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        if(meet.getEndDate() != null && meet.getEndDate().isBefore(LocalDateTime.now())){
            throw new BusinessException(ErrorCode.PLACE_VOTE_END);
        }

        List<PlaceVoteItem> placeVoteItemList = meet.getPlaceVote().getPlaceVoteItems();

        for(PlaceVoteItem item : placeVoteItemList){
            if(item.getPlace().equals(inDto.getPlace())){
                throw new BusinessException(ErrorCode.PLACE_VOTE_ITEM_DUPLICATED);
            }
        }

        PlaceVoteItem entity = placeVoteItemMapper.toEntity(inDto, meet.getPlaceVote(), user);
        PlaceVoteItem placeVoteItem = placeVoteItemRepository.save(entity);

        placeVoteItemList.add(placeVoteItem);
        placeVoteRepository.save(meet.getPlaceVote());

        String isVote = "false";
        if(placeVoteItem.getPlaceVoters().contains(user)){
            isVote = "true";
        }
        List<SimpleMemberResponseDto> memberList = new ArrayList<>();
        placeVoteItem.getPlaceVoters().forEach(member -> {
                    memberList.add(SimpleMemberResponseDto.builder()
                            .id(member.getId().toString())
                            .name(member.getName())
                            .build());
                }
        );

        return CreatePlaceVoteItemResponseDto.builder()
                .id(placeVoteItem.getId().toString())
                .place(placeVoteItem.getPlace())
                .editable("true")
                .isVote(isVote)
                .memberList(memberList)
                .build();
    }

    @Transactional
    public DeletePlaceVoteItemResponseDto deletePlaceVoteItem(DeletePlaceVoteItemRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        PlaceVoteItem placeVoteItem = placeVoteItemRepository.findById(inDto.getPlaceVoteItemId()).orElseThrow(
                () -> new BusinessException(ErrorCode.PLACE_VOTE_ITEM_NOT_EXISTS)
        );

        if(placeVoteItem.getPlaceVote().getMeet().getEndDate() != null && placeVoteItem.getPlaceVote().getMeet().getEndDate().isBefore(LocalDateTime.now())){
            throw new BusinessException(ErrorCode.PLACE_VOTE_END);
        }

        placeVoteItem.getPlaceVote().getPlaceVoteItems().remove(placeVoteItem);
        placeVoteItemRepository.delete(placeVoteItem);
        return null;
    }

    public UpdatePlaceVoteResponseDto updatePlaceVote(UpdatePlaceVoteRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        Meet meet = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        if(meet.getPlaceVote() != null && meet.getEndDate() != null && meet.getEndDate().isBefore(LocalDateTime.now())){
            throw new BusinessException(ErrorCode.PLACE_VOTE_END);
        }

        List<PlaceVoteItem> placeVoteItemList = meet.getPlaceVote().getPlaceVoteItems();

        for(Long id : inDto.getPlaceVoteItemList()){
            PlaceVoteItem placeVoteItem = placeVoteItemRepository.findById(id).orElseThrow(
                    () -> new BusinessException(ErrorCode.PLACE_VOTE_ITEM_NOT_EXISTS)
            );
        }

        for(PlaceVoteItem item : placeVoteItemList){
            List<Long> memberIds = item.getPlaceVoters().stream()
                    .map(Member::getId)
                    .toList();

            if(memberIds.contains(user.getId()) && !inDto.getPlaceVoteItemList().contains(item.getId())){
                item.getPlaceVoters().removeIf(member -> member.equals(user));
            }
            else if(!memberIds.contains(user.getId()) && inDto.getPlaceVoteItemList().contains(item.getId())){
                item.getPlaceVoters().add(user);
            }

            placeVoteItemRepository.save(item);
        }

        return null;
    }

    public FindPlaceVoteResponseDto findPlaceVote(FindPlaceVoteRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        Meet meet = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        String endDate = null;
        if(meet.getPlaceVote() != null){
            endDate = DateTimeUtils.formatWithOffset(meet.getEndDate());
        }
        Boolean isAuthor = meet.getAuthor().equals(user);

        return FindPlaceVoteResponseDto.builder()
                .meetTitle(meet.getTitle())
                .endDate(endDate)
                .isAuthor(isAuthor.toString())
                .build();
    }

}
