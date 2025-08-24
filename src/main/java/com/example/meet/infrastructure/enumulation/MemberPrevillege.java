package com.example.meet.infrastructure.enumulation;

import lombok.Getter;

@Getter
public enum MemberPrevillege {
    denied, accepted, admin;
    private String name;
}
