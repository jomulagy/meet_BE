package com.example.meet.vote.application.port.in;

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
import java.util.List;

public interface ScheduleVoteUseCase {
    FindScheduleVoteResponseDto findScheduleVote(FindScheduleVoteRequestDto inDto);

    List<FindScheduleVoteItemResponseDto> findScheduleVoteItemList(FindScheduleVoteItemRequestDto inDto);

    CreateScheduleVoteItemResponseDto createScheduleVoteItem(CreateScheduleVoteItemRequestDto inDto);

    DeleteScheduleVoteItemResponseDto deleteScheduleVoteItem(DeleteScheduleVoteItemRequestDto inDto);

    UpdateScheduleVoteResponseDto updateScheduleVote(UpdateScheduleVoteRequestDto inDto);
}
