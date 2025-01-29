package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.entity.Token;
import com.example.meet.repository.BatchLogRepository;
import com.example.meet.repository.TokenRepository;
import java.time.LocalDateTime;
import java.util.Map;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class RefreshAdminKakaoAcessToken extends CommonJob {
    @Value("${kakao.rest-api-key}")
    private String kakaoRestApiKey;
    @Value("${kakao.client-secret}")
    private String kakaoClientSecret;

    private final TokenRepository tokenRepository;

    @Autowired
    public RefreshAdminKakaoAcessToken(BatchLogRepository batchLogRepository, TokenRepository tokenRepository) {
        super(batchLogRepository);
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void performJob(JobExecutionContext context) {
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
                .then()
                .block();

        tokenRepository.save(kakaoToken);

        taskList.add("{accessToken = " + kakaoToken.getAccessToken() + ", refreshToken = " + kakaoToken.getRefreshToken() + "}");
    }
}
