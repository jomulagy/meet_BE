package com.example.meet.meet.adapter.out;

import static com.example.meet.meet.domain.entity.QMeet.meet;

import com.example.meet.meet.domain.entity.Meet;
import com.example.meet.meet.port.out.GetMeetEndDateBeforePort;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GetMeetEndDateBeforeAdapter implements GetMeetEndDateBeforePort {
    private final JPAQueryFactory query;

    @Override
    public List<Meet> get(LocalDateTime currentDate) {
        return query
                .selectFrom(meet)
                .where(
                        meet.endDate.lt(LocalDateTime.now()),
                        meet.date.isNull(),
                        meet.place.isNull()
                )
                .fetch();
    }
}
