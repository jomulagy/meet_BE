package com.example.meet.vote.adapter.in;

import static java.lang.Long.parseLong;

import com.example.meet.infrastructure.CommonResponse;
import com.example.meet.infrastructure.dto.request.place.CreatePlaceVoteItemRequestDto;
import com.example.meet.infrastructure.dto.request.place.DeletePlaceVoteItemRequestDto;
import com.example.meet.infrastructure.dto.request.place.FindPlaceVoteItemRequestDto;
import com.example.meet.infrastructure.dto.request.place.UpdatePlaceVoteRequestDto;
import com.example.meet.infrastructure.dto.response.place.CreatePlaceVoteItemResponseDto;
import com.example.meet.infrastructure.dto.response.place.DeletePlaceVoteItemResponseDto;
import com.example.meet.infrastructure.dto.response.place.FindPlaceVoteItemResponseDto;
import com.example.meet.place.adapter.in.dto.request.FindPlaceVoteRequestDto;
import com.example.meet.place.adapter.in.dto.response.FindPlaceVoteResponseDto;
import com.example.meet.infrastructure.dto.response.place.UpdatePlaceVoteResponseDto;
import com.example.meet.service.PlaceService;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/meet/place")
public class PlaceVoteController {
    private final PlaceService placeService;
    private final GetPlaceVoteUseCase getPlaceVoteUseCase;

    @GetMapping("")
    public CommonResponse<FindPlaceVoteResponseDto> findPlaceVote(@RequestParam(name = "meetId") String meetId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        FindPlaceVoteRequestDto inDto = FindPlaceVoteRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(parseLong(meetId))
                .build();

        return CommonResponse.success(getPlaceVoteUseCase.findPlaceVote(inDto));
    }

    @GetMapping("/item/list")
    public CommonResponse<List<FindPlaceVoteItemResponseDto>> findPlaceVoteItemList(@RequestParam(name = "meetId") String meetId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        FindPlaceVoteItemRequestDto inDto = FindPlaceVoteItemRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(parseLong(meetId))
                .build();

        return CommonResponse.success(placeService.findPlaceVoteItemList(inDto));
    }

    @PostMapping("/item")
    public CommonResponse<CreatePlaceVoteItemResponseDto> createPlaceVoteItem(@RequestBody CreatePlaceVoteItemRequestDto request){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        CreatePlaceVoteItemRequestDto inDto = CreatePlaceVoteItemRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(request.getMeetId())
                .place(request.getPlace())
                .build();

        return CommonResponse.success(placeService.createPlaceVoteItem(inDto));
    }

    @DeleteMapping("/item")
    public CommonResponse<DeletePlaceVoteItemResponseDto> deletePlaceVoteItem(@RequestParam String placeVoteItemId){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        DeletePlaceVoteItemRequestDto inDto = DeletePlaceVoteItemRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .placeVoteItemId(parseLong(placeVoteItemId))
                .build();

        return CommonResponse.success(placeService.deletePlaceVoteItem(inDto));
    }

    @PutMapping("")
    public CommonResponse<UpdatePlaceVoteResponseDto> updatePlaceVote(@RequestBody UpdatePlaceVoteRequestDto requestDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        UpdatePlaceVoteRequestDto inDto = UpdatePlaceVoteRequestDto.builder()
                .userId(parseLong(userDetails.getUsername()))
                .meetId(requestDto.getMeetId())
                .placeVoteItemList(requestDto.getPlaceVoteItemList())
                .build();

        return CommonResponse.success(placeService.updatePlaceVote(inDto));
    }
}
