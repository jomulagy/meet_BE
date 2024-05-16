package com.example.meet.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoResponseDto {
    @JsonProperty("id")
    private Long userID;
    @JsonProperty("properties")
    private Properties properties;  // 중첩된 객체에 대한 참조

    public static class Properties {
        private String nickname;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }
}
