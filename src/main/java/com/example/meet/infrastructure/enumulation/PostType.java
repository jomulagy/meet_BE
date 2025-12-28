package com.example.meet.infrastructure.enumulation;

import lombok.Getter;

@Getter
public enum PostType {
    MEET("회식"),
    TRAVEL("여행"),
    NOTIFICATION("공지"),
    VOTE("투표");

    private final String name;

    PostType(String name) {
        this.name = name;
    }
}
