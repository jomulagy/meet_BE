package com.example.meet.controller;

import com.example.meet.common.CommonResponse;
import com.example.meet.repository.TokenRepository;
import com.example.meet.service.AuthService;
import com.example.meet.service.MeetService;
import com.example.meet.service.ScheduleService;
import com.example.meet.service.TestService;
import java.net.UnknownHostException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
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
    private final Environment environment;
    @Value("${spring.profiles.active:}")
    private String activeProfiles;

    @GetMapping("/kakao/message")
    public Mono<String> kakaoMessageTest(){
        return testService.kakaoMessageTest("16");
    }

    @GetMapping("")
    public CommonResponse<Boolean> test() throws UnknownHostException {
        return CommonResponse.success(activeProfiles.contains("deploy"));
    }
}
