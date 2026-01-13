package com.example.meet.auth.application.service;

import com.example.meet.auth.application.port.in.AuthLoginUseCase;
import com.example.meet.member.application.domain.entity.Member;
import com.example.meet.infrastructure.auth.JwtTokenProvider;
import com.example.meet.infrastructure.auth.JwtTokenResponseDto;
import com.example.meet.auth.adapter.in.dto.in.KakaoTokenRequestDto;
import com.example.meet.infrastructure.dto.response.KakaoUserInfoResponseDto;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.MemberPrevillege;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.repository.MemberRepository;
import com.example.meet.token.application.port.out.TokenPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthLoginService implements AuthLoginUseCase {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    private final TokenPort tokenPort;

    public JwtTokenResponseDto login(KakaoTokenRequestDto request) {
        KakaoUserInfoResponseDto kakaoUserInfoResponseDto = getUserInfo(request.getAccessToken());

        if(!isUserExists(kakaoUserInfoResponseDto.getUserID())){
            register(kakaoUserInfoResponseDto);
        }

        Long userId = kakaoUserInfoResponseDto.getUserID();
        Member member = memberRepository.findById(userId).get();

        if(member.getId().equals(2927398983L)) {
            tokenPort.updateAdminKakaoRefreshToken(request.getRefreshToken());
        }

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
}
