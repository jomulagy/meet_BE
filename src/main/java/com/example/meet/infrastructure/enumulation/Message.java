package com.example.meet.infrastructure.enumulation;

import com.example.meet.infrastructure.dto.TemplateArgs;
import lombok.Getter;

@Getter
public enum Message {
    PARTICIPATE("108553"),
    VOTE("108568"), // 투표가 등록 되었습니다.
    POST("127222"), // 글 등록
    VOTE_ITEM_CREATED("129489"), // 투표 항목 등록
    VOTE_TERMINATE("127224"), // 투표 종료
    MEET_NOTIFICATION("110979"),
    DEPOSIT("115531"),
    PARTICIPANT_INPUT_REMIND("129508"), // 참여자 입력 알림
    DEPOSIT_PENALTY("129512"), // 연속 3회 불참 벌금 알림
    DEPOSIT_CHECK_ADMIN("129513"), // 관리자에게 입금 확인 요청
    PENALTY_BLOCK_ADMIN("116807"), // 벌금 미납 권한 차단 알림
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
