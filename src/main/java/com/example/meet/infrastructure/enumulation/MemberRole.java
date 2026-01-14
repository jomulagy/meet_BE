package com.example.meet.infrastructure.enumulation;

import lombok.Getter;

@Getter
public enum MemberRole {
    denied, accepted, admin;
    private String name;
}
