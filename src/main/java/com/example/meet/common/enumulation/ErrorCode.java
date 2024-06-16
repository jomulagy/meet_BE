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
    MEMBER_PERMITION_REQUIRED(HttpStatus.FORBIDDEN,"접근 권한이 없는 멤버 입니다."),
    MEMBER_NOT_EXISTS(HttpStatus.NOT_FOUND,"존재하지 않는 유저 입니다."),

    //기타
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR,"서버 에러"),

    //카카오 메세지 API
    JSON_CONVERT_ERROR(HttpStatus.BAD_REQUEST,"Failed to process JSON"),
    MEET_NOT_EXISTS(HttpStatus.NOT_FOUND, "존재하지 않는 모임 id 입니다.");




    private final HttpStatus httpStatus;
    private final String message;
}