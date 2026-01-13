package com.example.meet.api.message.application.port.in;

public interface SendMessageUseCase {
    void sendParticipate(String title, String id);

    void sendVoteCreated(String title, String id);

    void sendVoteTerminated(String title, String id);
}
