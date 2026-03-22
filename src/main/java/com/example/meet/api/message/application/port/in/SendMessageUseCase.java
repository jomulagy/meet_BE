package com.example.meet.api.message.application.port.in;

import com.example.meet.api.member.application.domain.entity.Member;

public interface SendMessageUseCase {
    void sendParticipate(String title, String id);

    void sendVoteCreated(String title, String id);

    void sendVoteItemCreated(String title, String id);

    void sendVoteTerminated(String title, String id);

    void sendParticipateVoteTerminated(String title, Long id);

    void sendParticipantInputReminder(String title, Long id);

    void sendDepositPenalty(Member member, String meetTitle, String deadline);

    void sendVoteEndReminder(String title, String id);
}
