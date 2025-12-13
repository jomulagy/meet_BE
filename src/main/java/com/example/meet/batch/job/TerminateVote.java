package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.entity.PlaceVote;
import com.example.meet.entity.ScheduleVote;
import com.example.meet.entity.ScheduleVoteItem;
import com.example.meet.infrastructure.dto.TemplateArgs;
import com.example.meet.infrastructure.enumulation.Message;
import com.example.meet.infrastructure.utils.MessageManager;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.meet.application.port.out.GetMeetPort;
import com.example.meet.infrastructure.repository.BatchLogRepository;
import com.example.meet.infrastructure.repository.MeetRepository;
import com.example.meet.infrastructure.repository.PlaceVoteRepository;
import com.example.meet.infrastructure.repository.ScheduleVoteRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.example.meet.meet.application.port.out.UpdateMeetPort;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.domain.entity.VoteItem;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class TerminateVote extends CommonJob {
    private final ScheduleVoteRepository scheduleVoteRepository;
    private final PlaceVoteRepository placeVoteRepository;
    private final MeetRepository meetRepository;
    private final GetMeetPort getMeetPort;
    private final UpdateMeetPort updateMeetPort;
    private final MessageManager messageManager;

    public TerminateVote(BatchLogRepository batchLogRepository, ScheduleVoteRepository scheduleVoteRepository,
                         PlaceVoteRepository placeVoteRepository, MeetRepository meetRepository,
                         GetMeetPort getMeetPort, UpdateMeetPort updateMeetPort, MessageManager messageManager) {
        super(batchLogRepository);
        this.scheduleVoteRepository = scheduleVoteRepository;
        this.placeVoteRepository = placeVoteRepository;
        this.meetRepository = meetRepository;
        this.getMeetPort = getMeetPort;
        this.updateMeetPort = updateMeetPort;
        this.messageManager = messageManager;
    }

    @Override
    @Transactional
    protected String performJob(JobExecutionContext context) {
        LocalDateTime currentDate = LocalDateTime.now();
        List<Meet> meetList = getMeetPort.getMeetEndDateBeforePort(currentDate);
        StringBuilder log = new StringBuilder();

        log.append("[");

        for(Meet meet : meetList){
            try {
                process(meet, log);
            } catch (Exception e) {
                continue;
            }
        }

        // 마지막 ", " 제거
        int index = log.lastIndexOf( ", ");

        if (index != -1 && index == log.length() - 2) {
            log.delete(index, log.length());
        }

        log.append("]");

        return log.toString();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void process(Meet meet, StringBuilder log) {
        try {

            Vote scheduleVote = meet.getScheduleVote();
            List<VoteItem> scheduleVoteItemList = scheduleVote.getVoteItems();

            if(scheduleVoteItemList.isEmpty()){
                return;
            }

            int max = -1;
            LocalDateTime result = scheduleVoteItemList.get(0).getDateTime();
            scheduleVoteItemList.sort(Comparator.comparing(VoteItem::getDateTime));

            for(VoteItem item : scheduleVoteItemList){
                if(item.getVoters().size() > max){
                    max = item.getVoters().size();
                    result = item.getDateTime();
                }
            }

            updateMeetPort.updateDate(meet.getId(), result);

        } catch (Exception e) {
            super.insertBatch("Fail to terminate ScheduleVote :: " + meet.getId(), "FAILURE", e.getMessage());
            throw e;
        }

        try {
            PlaceVote placeVote = meet.getPlaceVote();

            placeVote.setPlaceResult();
            placeVoteRepository.save(placeVote);

            meet.setPlace();

        } catch (Exception e) {
            super.insertBatch("Fail to terminate PlaceVote :: " + meet.getId(), "FAILURE", e.getMessage());
            throw e;
        }

        try {
            TemplateArgs templateArgs = TemplateArgs.builder()
                    .title(meet.getTitle())
                    .scheduleType(null)
                    .but(meet.getId().toString())
                    .build();
            Message.VOTE.setTemplateArgs(templateArgs);
            messageManager.sendAll(Message.VOTE).block();
            messageManager.sendMe(Message.VOTE).block();
        } catch (Exception e) {
            super.insertBatch("Fail to send participate vote message :: " + meet.getId(), "FAILURE", e.getMessage());
            throw e;
        }

        meetRepository.save(meet);

        log.append(meet.getTitle());
        log.append(", ");
    }
}
