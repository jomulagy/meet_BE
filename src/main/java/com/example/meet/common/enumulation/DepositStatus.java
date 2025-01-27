package com.example.meet.common.enumulation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DepositStatus {
    NONE(1,"미납"),
    PARTITION(2,"부분납"),
    OVERDUE(3, "연체"),
    COMPLETE(4,"완납")
    ;
    private final Integer code;
    private final String name;
}
