package com.example.meet.service;

import com.example.meet.common.auth.JwtTokenProvider;
import com.example.meet.common.dto.request.KakaoTokenRequestDto;
import com.example.meet.common.auth.JwtTokenResponseDto;
import com.example.meet.common.dto.response.KakaoUserInfoResponseDto;
import com.example.meet.common.exception.BusinessException;
import com.example.meet.common.enumulation.ErrorCode;
import com.example.meet.common.enumulation.MemberPrevillege;
import com.example.meet.entity.Member;
import com.example.meet.repository.MemberRepository;
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
public class AuthService {
    private final MemberRepository memberRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final JwtTokenProvider jwtTokenProvider;

    public JwtTokenResponseDto login(KakaoTokenRequestDto request) {
        KakaoUserInfoResponseDto kakaoUserInfoResponseDto = getUserInfo(request.getAccessToken());

        if(!isUserExists(kakaoUserInfoResponseDto.getUserID())){
            register(kakaoUserInfoResponseDto);
        }

        Long userId = kakaoUserInfoResponseDto.getUserID();
        Member member = memberRepository.findById(userId).get();
        if(member.getPrevillege() == MemberPrevillege.denied){
            throw new BusinessException(ErrorCode.MEMBER_PERMITION_REQUIRED);
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
