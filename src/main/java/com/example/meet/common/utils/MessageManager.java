package com.example.meet.common.utils;

import com.example.meet.common.dto.request.MessageRequestDto;
import com.example.meet.common.enumulation.ErrorCode;
import com.example.meet.common.enumulation.MemberPrevillege;
import com.example.meet.common.enumulation.Message;
import com.example.meet.common.exception.BusinessException;
import com.example.meet.entity.Member;
import com.example.meet.entity.Token;
import com.example.meet.repository.MemberRepository;
import com.example.meet.repository.TokenRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.List;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class MessageManager {
    @Value("${kakao.rest-api-key}")
    private String kakaoRestApiKey;
    @Value("${kakao.client-secret}")
    private String kakaoClientSecret;
    private final String REQUEST_URI = "http://localhost:5173/";
    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final ObjectMapper objectMapper;

    public Mono<String> sendAll(Message msg){
        WebClient webClient = WebClient.builder().build();
        String url = "https://kapi.kakao.com/v1/api/talk/friends/message/send";
        List<String> uuids = getAllUUIDs();

        MessageRequestDto messageRequestDto = MessageRequestDto.builder()
                .receiverUuids(uuids)
                .requestUrl(REQUEST_URI)
                .templateId(msg.getId())
                .templateArgs(msg.getTemplateArgs())
                .build();

        try{
            String receiverUuidsJson = convertListToJson(messageRequestDto.getReceiverUuids());
            String templateArgsJson = objectMapper.writeValueAsString(messageRequestDto.getTemplateArgs());

            return webClient.post()
                    .uri(url)
                    .headers(headers -> {
                        headers.setBearerAuth(getAccessToken());
                        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
                    })
                    .body(BodyInserters.fromFormData("receiver_uuids", receiverUuidsJson)
                            .with("request_url", messageRequestDto.getRequestUrl())
                            .with("template_id", messageRequestDto.getTemplateId())
                            .with("template_args", templateArgsJson))
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnError(WebClientResponseException.class, ex -> {
                        System.err.println("Error response: " + ex.getResponseBodyAsString());
                    });
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_CONVERT_ERROR);
        }
    }

    public String getAccessToken() {
        Token kakaoToken = tokenRepository.findByName("kakao");
        if (isAccessTokenExpired(kakaoToken)) {
            kakaoToken = refreshAccessToken(kakaoToken);
        }
        return kakaoToken.getAccessToken();
    }

    private boolean isAccessTokenExpired(Token accessToken) {
        return accessToken== null || accessToken.getExpiresIn() == null || LocalDateTime.now().isAfter(accessToken.getExpiresIn());
    }

    public Token refreshAccessToken(Token kakaoToken) {
        WebClient webClient = WebClient.builder().build();
        webClient.post()
                .uri("https://kauth.kakao.com/oauth/token")
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
                .then()
                .block();
        return kakaoToken;
    }

    private List<String> getAllUUIDs(){
        return memberRepository.findByPrevillegeGreaterThan(MemberPrevillege.denied)
                .stream()
                .map(Member::getUuid)
                .collect(Collectors.toList());
    }

    private String convertListToJson(List<String> list) {
        return "[" + String.join(",", list.stream()
                .map(s -> "\"" + s + "\"")
                .collect(Collectors.toList())) + "]";
    }
}
