package com.example.meet.meet.adapter.out;

import static com.example.meet.meet.application.domain.entity.QMeet.meet;

import com.example.meet.infrastructure.repository.MeetRepository;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.meet.application.port.out.GetMeetPort;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GetMeetAdapter implements GetMeetPort {
    private final JPAQueryFactory query;
    private final MeetRepository meetRepository;

    @Override
    public Optional<Meet> getMeetById(Long meetId) {
        return meetRepository.findById(meetId);
    }

    @Override
    public List<Meet> getMeetEndDateBeforePort(LocalDateTime currentDate) {
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
