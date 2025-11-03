package com.example.meet.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.meet.MeetApplication;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.entity.Member;
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
public class NotifyMeet {

    @Autowired
    private MeetRepository meetRepository;

    @Test
    void testTargetMeetList(){
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN).plusWeeks(1);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX).plusWeeks(1);
        List<Meet> meetList = meetRepository.findByDateBetween(startOfDay, endOfDay);

        assertEquals(1, meetList.size(), "200");
    }

    @Test
    void testTargetMemberList(){
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN).plusWeeks(1);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX).plusWeeks(1);
        List<Meet> meetList = meetRepository.findByDateBetween(startOfDay, endOfDay);

        Meet meet = meetList.get(0);
        List<Member> participantList = meet.getParticipants();

        assertEquals(7, participantList.size(), "200");
    }

}
