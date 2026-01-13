package com.example.meet.api.post.adapter.out;

import com.example.meet.infrastructure.enumulation.VoteStatus;
import com.example.meet.api.post.adapter.in.dto.in.UpdatePostRequestDto;
import com.example.meet.api.post.application.port.out.UpdatePostPort;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
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
    @Transactional
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
                .where(post.id.eq(inDto.getPostId()))
                .execute();
    }

    @Override
    @Transactional
    public void terminateVote(Long id) {
        jpaQueryFactory
                .update(post)
                .set(post.status, VoteStatus.FINISHED)
                .where(post.id.eq(id))
                .execute();
    }
}
