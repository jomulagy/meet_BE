package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.common.dto.TemplateArgs;
import com.example.meet.common.enumulation.Message;
import com.example.meet.common.utils.MessageManager;
import com.example.meet.entity.Meet;
import com.example.meet.repository.BatchLogRepository;
import com.example.meet.repository.MeetRepository;
import java.util.List;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

public class SendParticipateMessage extends CommonJob {
    private final MeetRepository meetRepository;
    private final MessageManager messageManager;

    @Autowired
    public SendParticipateMessage(
            BatchLogRepository batchLogRepository, MeetRepository meetRepository, MessageManager messageManager) {
        super(batchLogRepository);
        this.meetRepository = meetRepository;
        this.messageManager = messageManager;
    }

    @Override
    protected String performJob(JobExecutionContext context) {
        StringBuilder log = new StringBuilder();

        log.append("[");

        List<Meet> meetList = meetRepository.findByDateIsNotNullAndPlaceIsNotNullAndParticipantsNumIsNull();

        for (Meet meet : meetList) {
            TemplateArgs templateArgs = TemplateArgs.builder()
                    .title(meet.getTitle())
                    .scheduleType(null)
                    .but(meet.getId().toString())
                    .build();
            Message.VOTE.setTemplateArgs(templateArgs);
            messageManager.sendAll(Message.VOTE).block();
            messageManager.sendMe(Message.VOTE).block();

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
