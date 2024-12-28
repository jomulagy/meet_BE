package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.common.dto.TemplateArgs;
import com.example.meet.common.enumulation.Message;
import com.example.meet.common.utils.MessageManager;
import com.example.meet.entity.Meet;
import com.example.meet.entity.Member;
import com.example.meet.repository.BatchLogRepository;
import com.example.meet.repository.MeetRepository;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

public class NotifyMeet extends CommonJob {
    private final MeetRepository meetRepository;
    private final MessageManager messageManager;

    @Autowired
    public NotifyMeet(
            BatchLogRepository batchLogRepository, MeetRepository meetRepository, MessageManager messageManager) {
        super(batchLogRepository);
        this.meetRepository = meetRepository;
        this.messageManager = messageManager;
    }

    @Override
    @Transactional
    protected String performJob(JobExecutionContext context) {
        StringBuilder log = new StringBuilder();

        //오늘 날짜인 entity 조회
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN).plusWeeks(1);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX).plusWeeks(1);
        List<Meet> meetList = meetRepository.findByDateBetween(startOfDay, endOfDay);

        log.append("[");

        //알림 전송
        for (Meet meet : meetList) {
            // 참여자 리스트 조회
            List<Member> participantList = meet.getParticipants();

            // 메세지 템플릿 설정
            TemplateArgs templateArgs = TemplateArgs.builder()
                    .title(meet.getTitle())
                    .but(meet.getId().toString())
                    .scheduleType(null)
                    .build();
            Message.MEET_NOTIFICATION.setTemplateArgs(templateArgs);

            // 메세지 전송
            for (Member member : participantList) {
                if (member.getId().equals(2927398983L)) {
                    messageManager.sendMe(Message.MEET_NOTIFICATION).block();
                } else {
                    messageManager.send(Message.MEET_NOTIFICATION, member).block();
                }
            }

            log.append(meet.getId());
            log.append(", ");
        }

        // 마지막 ", " 제거
        int index = log.lastIndexOf( ", ");

        if (index != -1 && index == log.length() - 2) {
            log.delete(index, log.length());
        }

        log.append("]");

        return log.toString();
    }
}
