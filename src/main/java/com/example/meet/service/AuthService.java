package com.example.meet.service;

import com.example.meet.infrastructure.dto.request.Batch.BatchRequestDto;
import com.example.meet.infrastructure.dto.response.AdminAccessTokenResponseDto;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.MemberPrevillege;
import com.example.meet.entity.Member;
import com.example.meet.entity.Token;
import com.example.meet.infrastructure.repository.MemberRepository;
import com.example.meet.infrastructure.repository.TokenRepository;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    @Value("${kakao.rest-api-key}")
    private String kakaoRestApiKey;
    @Value("${kakao.client-secret}")
    private String kakaoClientSecret;

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;



    public AdminAccessTokenResponseDto findAdminAccessToken(Long userId) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(userId).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인 (관리자 여부)
        if(!user.getPrevillege().equals(MemberPrevillege.admin)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        return AdminAccessTokenResponseDto.builder()
                .adminAccessToken(getAccessToken())
                .build();
    }

    private boolean isAccessTokenExpired(Token accessToken) {
        return accessToken== null || accessToken.getExpiresIn() == null || LocalDateTime.now().isAfter(accessToken.getExpiresIn());
    }

    public String getAccessToken() {
        Token kakaoToken = tokenRepository.findByName("kakao");
        if (isAccessTokenExpired(kakaoToken)) {
            kakaoToken = refreshAccessToken();
        }
        return kakaoToken.getAccessToken();
    }

    @Transactional
    public Token refreshAccessToken() {
        Token kakaoToken = tokenRepository.findByName("kakao");
        WebClient webClient = WebClient.builder().build();
        webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
                .headers(headers -> headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
                .body(BodyInserters.fromFormData("grant_type", "refresh_token")
                        .with("client_id", kakaoRestApiKey)
                        .with("refresh_token", kakaoToken.getRefreshToken())
                        .with("client_secret", kakaoClientSecret)
                )
                .retrieve()
                .bodyToMono(Map.class)
                .doOnNext(response -> {
                    kakaoToken.setAccessToken((String) response.get("access_token"));
                    int expiresIn = (Integer) response.get("expires_in");
                    kakaoToken.setExpiresIn(LocalDateTime.now().plusSeconds(expiresIn));
                    if (response.containsKey("refresh_token")) {
                        kakaoToken.setRefreshToken((String) response.get("refresh_token"));
                    }
                })
                .doOnError(WebClientResponseException.class, ex -> {
                    log.error(ex.getStatusCode().toString() + ex.getResponseBodyAsString());
                })
                .onErrorResume(error -> {
                    return null;
                })
                .then()
                .block();

                tokenRepository.save(kakaoToken);
        return kakaoToken;
    }

    public void isAdmin(BatchRequestDto inDto){
        String accessToken = tokenRepository.findByName("kakao").getAccessToken();

        if(!accessToken.equals(inDto.getToken())){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }
    }
}
