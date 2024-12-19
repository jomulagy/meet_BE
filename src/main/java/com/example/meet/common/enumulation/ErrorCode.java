package com.example.meet.common.enumulation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    //인증
    JWT_TOKEN_NOT_EXISTS(HttpStatus.UNAUTHORIZED,"토큰 정보가 없습니다."),
    JWT_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED,"토큰 기한이 만료 되었습니다."),
    JWT_TOKEN_INVALID(HttpStatus.UNAUTHORIZED,"유효하지 않은 토큰 입니다."),

    //유저
    MEMBER_PERMISSION_REQUIRED(HttpStatus.FORBIDDEN,"접근 권한이 없는 멤버 입니다."),
    MEMBER_NOT_EXISTS(HttpStatus.NOT_FOUND,"존재하지 않는 유저 입니다."),
    VALUE_REQUIRED(HttpStatus.BAD_REQUEST, "빈 값을 입력 할 수 없습니다."),

    //기타
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"서버 에러"),

    //카카오 메세지 API
    JSON_CONVERT_ERROR(HttpStatus.BAD_REQUEST,"Failed to process JSON"),

    //모임
    MEET_NOT_EXISTS(HttpStatus.NOT_FOUND, "존재하지 않는 모임 id 입니다."),
    MEET_EDIT_PERMISSION_REQUIRED(HttpStatus.FORBIDDEN, "편집 권한이 없는 유저 입니다."),
    VOTE_REQUIRED(HttpStatus.BAD_REQUEST, "투표한 필드는 편집 할 수 없습니다."),

    SCHEDULE_VOTE_ITEM_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 존재하는 날짜 입니다."),

    SCHEDULE_VOTE_END(HttpStatus.BAD_REQUEST, "종료된 투표 입니다."),
    SCHEDULE_VOTE_ITEM_NOT_EXISTS(HttpStatus.NOT_FOUND, "존재하지 않는 투표 항목 입니다."),

    PLACE_VOTE_END(HttpStatus.BAD_REQUEST, "종료된 투표 입니다."),
    PLACE_VALUE_REQUIRED(HttpStatus.BAD_REQUEST, "빈 값을 입력 할 수 없습니다."),
    PLACE_VOTE_ITEM_DUPLICATED(HttpStatus.BAD_REQUEST, "이미 존재하는 장소 입니다."),
    PLACE_VOTE_ITEM_NOT_EXISTS(HttpStatus.NOT_FOUND, "존재하지 않는 투표 항목 입니다."),

    PARTICIPATE_VOTE_END(HttpStatus.BAD_REQUEST, "종료된 투표 입니다."),
    PARTICIPAT_VOTE_ITEM_NOT_EXISTS(HttpStatus.NOT_FOUND, "존재하지 않는 투표 항목 입니다."),

    //메일
    MAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "메일 전송중 오류가 발생했습니다."),

    ;

    private final HttpStatus httpStatus;
    private final String message;
}
