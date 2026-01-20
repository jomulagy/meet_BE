package com.example.meet.api.post.adapter.out;

import com.example.meet.api.post.application.domain.entity.Post;
import com.example.meet.api.post.application.port.out.GetPostPort;
import com.example.meet.infrastructure.enumulation.PostType;
import com.example.meet.infrastructure.repository.MeetRepository;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.meet.api.auth.application.domain.entity.QPrivilege.privilege;
import static com.example.meet.api.post.application.domain.entity.QPost.post;

@Repository
@RequiredArgsConstructor
public class GetPostAdapter implements GetPostPort {
    private final JPAQueryFactory query;
    private final MeetRepository meetRepository;

    @Override
    public Optional<Post> getPostById(Long meetId) {
        return meetRepository.findById(meetId);
    }

    @Override
    public List<Post> findAll() {
        return query
                .selectFrom(post)
                .orderBy(post.id.desc())
                .fetch();
    }

    @Override
    public List<Post> findListByType(PostType type, Long memberId) {
        DateTimeExpression<LocalDateTime> startDateTime =
                Expressions.dateTimeTemplate(
                        LocalDateTime.class,
                        "{0}",
                        privilege.startDate
                );

        DateTimeExpression<LocalDateTime> endDateTime =
                Expressions.dateTimeTemplate(
                        LocalDateTime.class,
                        "DATE_ADD({0}, INTERVAL 1 DAY)",
                        privilege.endDate
                );

        return query
                .selectFrom(post)
                .where(
                        JPAExpressions
                                .selectOne()
                                .from(privilege)
                                .where(
                                        privilege.member.id.eq(memberId),
                                        post.createdAt.goe(startDateTime),
                                        post.createdAt.lt(endDateTime)
                                )
                                .exists(),
                        post.type.eq(type)
                )
                .orderBy(post.id.desc())
                .fetch();
    }
}
