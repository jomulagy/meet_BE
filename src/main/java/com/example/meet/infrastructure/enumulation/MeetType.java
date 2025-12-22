package com.example.meet.infrastructure.enumulation;

import lombok.Getter;

@Getter
public enum MeetType {
    MEET("회식"),
    TRAVEL("여행")
    ;

    private final String name;

    MeetType(String name) {
        this.name = name;
    }
}
