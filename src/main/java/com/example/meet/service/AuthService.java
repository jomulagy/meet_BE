package com.example.meet.service;

import com.example.meet.common.auth.JwtTokenProvider;
import com.example.meet.common.dto.request.KakaoTokenRequestDto;
import com.example.meet.common.auth.JwtTokenResponseDto;
import com.example.meet.common.dto.response.AdminAccessTokenResponseDto;
import com.example.meet.common.dto.response.KakaoUserInfoResponseDto;
import com.example.meet.common.exception.BusinessException;
import com.example.meet.common.enumulation.ErrorCode;
import com.example.meet.common.enumulation.MemberPrevillege;
import com.example.meet.common.utils.LoggerManager;
import com.example.meet.entity.BatchLog;
import com.example.meet.entity.Member;
import com.example.meet.entity.Token;
import com.example.meet.repository.BatchLogRepository;
import com.example.meet.repository.MemberRepository;
import com.example.meet.repository.TokenRepository;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
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
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;
    private final LoggerManager loggerManager;

    public JwtTokenResponseDto login(KakaoTokenRequestDto request) {
        KakaoUserInfoResponseDto kakaoUserInfoResponseDto = getUserInfo(request.getAccessToken());

        if(!isUserExists(kakaoUserInfoResponseDto.getUserID())){
            register(kakaoUserInfoResponseDto);
        }

        Long userId = kakaoUserInfoResponseDto.getUserID();
        Member member = memberRepository.findById(userId).get();
        if(member.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }
        // jwt 토큰 반환
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userId,"");
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        JwtTokenResponseDto jwtToken = jwtTokenProvider.generateToken(authentication);
        return jwtToken;
    }

    private void register(KakaoUserInfoResponseDto kakaoUserInfoResponseDto) {
        Member member = Member.builder()
                .id(kakaoUserInfoResponseDto.getUserID())
                .name(kakaoUserInfoResponseDto.getProperties().getNickname())
                .email(kakaoUserInfoResponseDto.getKakaoAccount().getEmail())
                .previllege(MemberPrevillege.denied)
                .build();
        memberRepository.save(member);
    }

    private boolean isUserExists(Long userID) {
        return memberRepository.existsById(userID);
    }

    private KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        WebClient webClient = WebClient.builder().build();
        String url = "https://kapi.kakao.com/v2/user/me";

        try{
            KakaoUserInfoResponseDto response = webClient.get()
                    .uri(url)    // 요청 경로 설정
                    .header("Content-type", "application/x-www-form-urlencoded;charset=utf-8")
                    .header("Authorization",accessToken)
                    // body도 메소드에 따라 추가
                    .retrieve()    // 여기 전까지가 요청을 정의 한 부분
                    // 여기부터 정의하는건 응답을 어떻게 처리할 것인지
                    .bodyToMono(KakaoUserInfoResponseDto.class)    // 응답이 한번 돌아오고, 응답의 body를 String으로 해석
                    .block();
            return response;
        } catch (BusinessException e){
            log.error("AuthService : kakao API returned Exception");
            return null;
        }
        
    }

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

    @Scheduled(cron = "0 0 0 * * *")
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
                    log.debug("Response: {}", response);
                    kakaoToken.setAccessToken((String) response.get("access_token"));
                    int expiresIn = (Integer) response.get("expires_in");
                    kakaoToken.setExpiresIn(LocalDateTime.now().plusSeconds(expiresIn));
                    if (response.containsKey("refresh_token")) {
                        kakaoToken.setRefreshToken((String) response.get("refresh_token"));
                    }
                })
                .doOnError(WebClientResponseException.class, ex -> {
                    loggerManager.insertBatch("refreshAccessToken", ex.getStatusCode().toString(), ex.getResponseBodyAsString());
                })
                .onErrorResume(error -> {
                    return null;
                })
                .then()
                .block();

                tokenRepository.save(kakaoToken);
                loggerManager.insertBatch("refreshAccessToken", "200", "success");
        return kakaoToken;
    }
}
