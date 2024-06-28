package com.example.meet.controller;

import com.example.meet.common.CommonResponse;
import com.example.meet.service.ScheduleService;
import com.example.meet.service.TestService;
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

    @GetMapping("/kakao/message")
    public Mono<String> kakaoMessageTest(){
        return testService.kakaoMessageTest();
    }

    @GetMapping("")
    public void test(){
        scheduleService.terminateScheduleVote();
    }
}
