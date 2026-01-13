package com.example.meet.api.token.adapter.out;

import static com.example.meet.entity.QToken.token;

import com.example.meet.api.token.application.port.out.TokenPort;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class TokenAdapter implements TokenPort {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    @Transactional
    public void updateAdminKakaoRefreshToken(String refreshToken) {
        jpaQueryFactory
                .update(token)
                .set(token.refreshToken, refreshToken)
                .where(
                        token.id.eq(1L)
                )
                .execute();
    }
}
