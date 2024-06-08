package com.example.meet.common.enumulation;

import lombok.Getter;

@Getter
public enum MemberPrevillege {
    denied, accepted, admin;
    private String name;
}
