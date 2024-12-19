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
import com.example.meet.service.AuthService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.socket.DatagramChannel;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final String REQUEST_URI = "http://43.203.36.37/";
    private final MemberRepository memberRepository;
    private final ObjectMapper objectMapper;
    private final AuthService authService;

    public Mono<String> sendAll(Message msg){
        WebClient webClient = WebClient.builder().build();
        String url = "https://kapi.kakao.com/v1/api/talk/friends/message/send";
        List<String> uuids = getAllUUIDs();

        if(uuids.isEmpty()){
            return Mono.empty();
        }

        MessageRequestDto messageRequestDto = MessageRequestDto.builder()
                .receiverUuids(uuids)
                .requestUrl(REQUEST_URI)
                .templateId(msg.getId())
                .templateArgs(msg.getTemplateArgs())
                .build();

        try{
            String receiverUuidsJson = convertListToJson(messageRequestDto.getReceiverUuids());
            String templateArgsJson = objectMapper.writeValueAsString(messageRequestDto.getTemplateArgs());

            Mono<String> response = webClient.post()
                    .uri(url)
                    .headers(headers -> {
                        headers.setBearerAuth(authService.getAccessToken());
                        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
                    })
                    .body(BodyInserters.fromFormData("receiver_uuids", receiverUuidsJson)
                            .with("request_url", messageRequestDto.getRequestUrl())
                            .with("template_id", messageRequestDto.getTemplateId())
                            .with("template_args", templateArgsJson))
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnError(WebClientResponseException.class, ex -> {
                        try {
                            String errorBody = ex.getResponseBodyAsString();
                            System.err.println("Error response: " + errorBody);
                        } catch (Exception e) {
                            System.err.println("Error reading response body: " + e.getMessage());
                        }
                    })
                    .onErrorResume(WebClientResponseException.class, ex -> {
                        System.err.println("Caught WebClientResponseException: " + ex.getMessage());
                        return Mono.error(ex);
                    });
            return response;
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_CONVERT_ERROR);
        }
    }

    public Mono<String> send(Message message, Member member) {
        if(member.getUuid() == null){
            return Mono.empty();
        }

        WebClient webClient = WebClient.builder().build();
        String url = "https://kapi.kakao.com/v1/api/talk/friends/message/send";

        List<String> uuids = new ArrayList<>();
        uuids.add(member.getUuid());

        MessageRequestDto messageRequestDto = MessageRequestDto.builder()
                .receiverUuids(uuids)
                .requestUrl(REQUEST_URI)
                .templateId(message.getId())
                .templateArgs(message.getTemplateArgs())
                .build();

        try{
            String receiverUuidsJson = convertListToJson(messageRequestDto.getReceiverUuids());
            String templateArgsJson = objectMapper.writeValueAsString(messageRequestDto.getTemplateArgs());

            Mono<String> response = webClient.post()
                    .uri(url)
                    .headers(headers -> {
                        headers.setBearerAuth(authService.getAccessToken());
                        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
                    })
                    .body(BodyInserters.fromFormData("receiver_uuids", receiverUuidsJson)
                            .with("request_url", messageRequestDto.getRequestUrl())
                            .with("template_id", messageRequestDto.getTemplateId())
                            .with("template_args", templateArgsJson))
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnError(WebClientResponseException.class, ex -> {
                        try {
                            String errorBody = ex.getResponseBodyAsString();
                            System.err.println("Error response: " + errorBody);
                        } catch (Exception e) {
                            System.err.println("Error reading response body: " + e.getMessage());
                        }
                    })
                    .onErrorResume(WebClientResponseException.class, ex -> {
                        System.err.println("Caught WebClientResponseException: " + ex.getMessage());
                        return Mono.error(ex);
                    });
            return response;
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_CONVERT_ERROR);
        }
    }

    public Mono<String> sendMe(Message message) {
        WebClient webClient = WebClient.builder().build();
        String url = "https://kapi.kakao.com/v2/api/talk/memo/send";

        MessageRequestDto messageRequestDto = MessageRequestDto.builder()
                .requestUrl(REQUEST_URI)
                .templateId(message.getId())
                .templateArgs(message.getTemplateArgs())
                .build();

        try{
            String templateArgsJson = objectMapper.writeValueAsString(messageRequestDto.getTemplateArgs());

            Mono<String> response = webClient.post()
                    .uri(url)
                    .headers(headers -> {
                        headers.setBearerAuth(authService.getAccessToken());
                        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);
                    })
                    .body(BodyInserters.fromFormData("template_id", messageRequestDto.getTemplateId()) // template_id 전달
                            .with("template_args", templateArgsJson))
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnError(WebClientResponseException.class, ex -> {
                        try {
                            String errorBody = ex.getResponseBodyAsString();
                            System.err.println("Error response: " + errorBody);
                        } catch (Exception e) {
                            System.err.println("Error reading response body: " + e.getMessage());
                        }
                    })
                    .onErrorResume(WebClientResponseException.class, ex -> {
                        System.err.println("Caught WebClientResponseException: " + ex.getMessage());
                        return Mono.error(ex);
                    });
            return response;
        } catch (JsonProcessingException e) {
            throw new BusinessException(ErrorCode.JSON_CONVERT_ERROR);
        }
    }

    private List<String> getAllUUIDs(){
        return memberRepository.findByPrevillegeGreaterThanAndUuidIsNotNull(MemberPrevillege.denied)
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
