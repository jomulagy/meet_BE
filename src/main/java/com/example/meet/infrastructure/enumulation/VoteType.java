package com.example.meet.infrastructure.enumulation;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum VoteType {
    DATE,
    PLACE,
    TEXT;

    @JsonCreator
    public static VoteType from(String value) {
        return VoteType.valueOf(value.toUpperCase());
    }

    public static String of(VoteType voteType) {
        return voteType.toString().toLowerCase();
    }
}
