package com.example.meet.meet.adapter.out;

import com.example.meet.meet.application.port.out.UpdateMeetPort;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

import static com.example.meet.meet.application.domain.entity.QMeet.meet;

@Repository
@RequiredArgsConstructor
public class UpdateMeetAdapter implements UpdateMeetPort {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public long updateDate(Long meetId, LocalDateTime dateTime) {
        return jpaQueryFactory
                .update(meet)
                .set(meet.date, dateTime)
                .where(
                        meet.id.eq(meetId)
                )
                .execute();
    }
}
