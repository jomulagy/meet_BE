package com.example.meet.service;

import com.example.meet.common.dto.TemplateArgs;
import com.example.meet.common.enumulation.Message;
import com.example.meet.common.utils.MessageManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TestService {
    private final MessageManager messageManager;

    public Mono<String> kakaoMessageTest() {
        TemplateArgs templateArgs = TemplateArgs.builder()
                .title("test")
                .scheduleType(null)
                .build();
        Message.VOTE.setTemplateArgs(templateArgs);
        Mono<String> response = messageManager.sendAll(Message.VOTE);
        log.info(response.toString());
        return response;
    }
}