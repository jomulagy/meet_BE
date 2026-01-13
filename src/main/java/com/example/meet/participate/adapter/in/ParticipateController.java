package com.example.meet.participate.adapter.in;

import static java.lang.Long.parseLong;

import com.example.meet.infrastructure.CommonResponse;
import com.example.meet.participate.adapter.in.dto.in.FindParticipateVoteRequestDto;
import com.example.meet.infrastructure.dto.response.participate.FindParticipateVoteResponseDto;
import com.example.meet.participate.adapter.in.dto.in.TerminateParticipateVoteRequestDto;
import com.example.meet.participate.adapter.in.dto.in.VoteParticipateVoteRequestDto;
import com.example.meet.participate.application.port.in.DeleteParticipateUseCase;
import com.example.meet.participate.application.port.in.GetParticipateUseCase;
import com.example.meet.participate.application.port.in.UpdateParticipateVoteUseCase;
import com.example.meet.service.ParticipateService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/participate")
public class ParticipateController {
    private final ParticipateService participateService;
    private final GetParticipateUseCase getParticipateUseCase;
    private final UpdateParticipateVoteUseCase updateParticipateVoteUseCase;
    private final DeleteParticipateUseCase deleteParticipateUseCase;

    @GetMapping("")
    public CommonResponse<FindParticipateVoteResponseDto> findParticipateVote(@RequestParam(name = "postId") String postId){
        FindParticipateVoteRequestDto inDto = FindParticipateVoteRequestDto.builder()
                .postId(parseLong(postId))
                .build();

        return CommonResponse.success(getParticipateUseCase.get(inDto));
    }

    @DeleteMapping("")
    public CommonResponse<Void> delete(@RequestParam(name = "postId") Long postId){
        deleteParticipateUseCase.delete(postId);
        return CommonResponse.success();
    }

    @PostMapping("/terminate")
    public CommonResponse<Void> terminate(@RequestBody TerminateParticipateVoteRequestDto inDto){
        updateParticipateVoteUseCase.terminate(inDto);
        return CommonResponse.success();
    }

    @PostMapping("/vote")
    public CommonResponse<Void> vote(@RequestBody VoteParticipateVoteRequestDto inDto){
        updateParticipateVoteUseCase.vote(inDto);
        return CommonResponse.success();
    }

}
