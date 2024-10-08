package com.example.meet.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class KakaoUserInfoResponseDto {
    @JsonProperty("id")
    private Long userID;
    @JsonProperty("properties")
    private Properties properties;  // 중첩된 객체에 대한 참조
    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    public static class Properties {
        private String nickname;

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class KakaoAccount {
        private String email;
    }
}
