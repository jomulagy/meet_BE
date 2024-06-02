package com.example.meet.common.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class KakaoFriendsInfoResponseDto {
    private String after_url;
    private List<KakaoFriendsInfoDto> elements;

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Builder
    public static class KakaoFriendsInfoDto {
        private String id;
        private String uuid;
        private String favorite;
        private String profile_nickname;
        private String profile_thumbnail_image;
    }
}
