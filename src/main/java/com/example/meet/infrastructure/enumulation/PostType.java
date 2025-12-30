package com.example.meet.infrastructure.enumulation;

import lombok.Getter;

import java.util.Arrays;

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

    public static PostType fromName(String name) {
        return Arrays.stream(values())
                .filter(type -> type.getName().equals(name))
                .findFirst()
                .orElseThrow(() ->
                        new IllegalArgumentException("지원하지 않는 PostType name 입니다: " + name)
                );
    }
}
