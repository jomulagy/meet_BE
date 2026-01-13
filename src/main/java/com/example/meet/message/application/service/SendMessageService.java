package com.example.meet.message.application.service;

import com.example.meet.infrastructure.dto.TemplateArgs;
import com.example.meet.infrastructure.enumulation.Message;
import com.example.meet.infrastructure.utils.MessageManager;
import com.example.meet.message.application.port.in.SendMessageUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendMessageService implements SendMessageUseCase {
    private final MessageManager messageManager;

    @Override
    public void sendParticipate(String title, String id) {
        TemplateArgs templateArgs = TemplateArgs.builder()
                .title(title)
                .scheduleType(null)
                .but(id)
                .build();
        Message.PARTICIPATE.setTemplateArgs(templateArgs);

        messageManager.sendAll(Message.PARTICIPATE).block();
    }

    @Override
    public void sendVoteCreated(String title, String id) {
        TemplateArgs templateArgs = TemplateArgs.builder()
                .title(title)
                .but(id)
                .scheduleType(null)
                .build();


        Message.VOTE.setTemplateArgs(templateArgs);
        messageManager.sendAll(Message.VOTE).block();
    }

    @Override
    public void sendVoteTerminated(String title, String id) {
        TemplateArgs templateArgs = TemplateArgs.builder()
                .title(title)
                .but(id)
                .scheduleType(null)
                .build();

        Message.VOTE_TERMINATE.setTemplateArgs(templateArgs);
        messageManager.sendAll(Message.VOTE_TERMINATE).block();
    }
}
