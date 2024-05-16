package com.example.meet.common.variables;

import lombok.Getter;

@Getter
public enum MemberPrevillege {
    denied, accepted, admin;
    private String name;
}
