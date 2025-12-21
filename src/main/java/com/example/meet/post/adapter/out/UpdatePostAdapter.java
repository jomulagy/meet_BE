package com.example.meet.post.adapter.out;

import com.example.meet.post.adapter.in.dto.in.UpdatePostRequestDto;
import com.example.meet.post.application.port.out.UpdatePostPort;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static com.example.meet.post.application.domain.entity.QPost.post;


@Repository
@RequiredArgsConstructor
public class UpdatePostAdapter implements UpdatePostPort {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public long update(UpdatePostRequestDto inDto) {
        LocalDateTime dateTime;

        if (inDto.getDate() == null) {
            dateTime = null;
        } else {
            LocalTime time = (inDto.getTime() != null) ? inDto.getTime() : LocalTime.of(0, 0);
            dateTime = LocalDateTime.of(inDto.getDate(), time);
        }

        return jpaQueryFactory
                .update(post)
                .set(post.title, inDto.getTitle())
                .set(post.content, inDto.getContent())
                .set(post.date, dateTime)
                .set(post.place, inDto.getPlace())
                .where(post.id.eq(inDto.getPostId()))
                .execute();
    }

    @Override
    public void updateDate(Long id, LocalDateTime dateTime) {
        jpaQueryFactory
                .update(post)
                .set(post.date, dateTime)
                .where(post.id.eq(id))
                .execute();
    }

    @Override
    public void updatePlace(Long id, String place) {
        jpaQueryFactory
                .update(post)
                .set(post.place, place)
                .where(post.id.eq(id))
                .execute();
    }
}
