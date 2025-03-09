package com.example.meet.controller;

import com.example.meet.common.CommonResponse;
import com.example.meet.common.dto.request.CreateMeetRequestDto;
import com.example.meet.entity.Token;
import com.example.meet.repository.TokenRepository;
import com.example.meet.service.AuthService;
import com.example.meet.service.MeetService;
import com.example.meet.service.ScheduleService;
import com.example.meet.service.TestService;
import java.net.InetAddress;
import java.net.UnknownHostException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {
    private final TestService testService;
    private final ScheduleService scheduleService;
    private final MeetService meetService;
    private final AuthService authService;
    private final TokenRepository tokenRepository;

    @GetMapping("/kakao/message")
    public Mono<String> kakaoMessageTest(){
        return testService.kakaoMessageTest("16");
    }

    @GetMapping("")
    public CommonResponse<String> test() throws UnknownHostException {
        String v = InetAddress.getLocalHost().getHostAddress();
        return CommonResponse.success(v);
    }
}
