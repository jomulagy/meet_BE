package com.example.meet.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.meet.MeetApplication;
import com.example.meet.post.application.domain.entity.Post;
import com.example.meet.member.application.domain.entity.Member;
import com.example.meet.infrastructure.repository.MeetRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = MeetApplication.class)
@ActiveProfiles("local")
public class NotifyPost {

    @Autowired
    private MeetRepository meetRepository;

    @Test
    void testTargetMeetList(){
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN).plusWeeks(1);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX).plusWeeks(1);
        List<Post> postList = meetRepository.findByDateBetween(startOfDay, endOfDay);

        assertEquals(1, postList.size(), "200");
    }

    @Test
    void testTargetMemberList(){
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN).plusWeeks(1);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX).plusWeeks(1);
        List<Post> postList = meetRepository.findByDateBetween(startOfDay, endOfDay);

        Post post = postList.get(0);
        List<Member> participantList = post.getParticipants();

        assertEquals(7, participantList.size(), "200");
    }

}
