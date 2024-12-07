package com.example.meet.controller;

import com.example.meet.common.CommonResponse;
import com.example.meet.common.dto.request.Batch.BatchRequestDto;
import com.example.meet.common.dto.response.meet.CreateMeetResponseDto;
import com.example.meet.service.AuthService;
import com.example.meet.service.MeetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/batch")
public class BatchController {
    private final MeetService meetService;
    private final AuthService authService;

    @PostMapping("/create-meet")
    CommonResponse<CreateMeetResponseDto> createMeet(@RequestBody BatchRequestDto inDto) {
        authService.isAdmin(inDto);
        meetService.createRoutineMeet();
        return CommonResponse.success();
    }
}
