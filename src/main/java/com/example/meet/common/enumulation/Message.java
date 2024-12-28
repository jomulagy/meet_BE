package com.example.meet.common.enumulation;

import com.example.meet.common.dto.TemplateArgs;
import lombok.Getter;

@Getter
public enum Message {
    VOTE("108553"),
    SCHEDULE("108568"),
    MEET_NOTIFICATION("110979"),
    DEPOSIT("115531"),
    ;

    private String id;
    private TemplateArgs templateArgs;

    Message(String id) {
        this.id = id;
    }

    public void setTemplateArgs(TemplateArgs templateArgs) {
        this.templateArgs = templateArgs;
    }
}
