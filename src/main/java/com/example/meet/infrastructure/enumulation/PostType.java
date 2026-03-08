package com.example.meet.infrastructure.enumulation;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum PostType {
    MEET("회식", true),
    TRAVEL("여행", true),
    NOTIFICATION("공지", false),
    VOTE("투표", false);

    private final String name;
    private final boolean participationVote;

    PostType(String name, boolean participationVote) {
        this.name = name;
        this.participationVote = participationVote;
    }

    public boolean requiresParticipation() {
        return participationVote;
    }

    public static PostType fromName(String name) {
        return Arrays.stream(values())
                .filter(type -> type.getName().equals(name))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("지원하지 않는 PostType name 입니다: " + name)
                );
    }
}
