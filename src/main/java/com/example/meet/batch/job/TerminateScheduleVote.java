package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.entity.Meet;
import com.example.meet.entity.ScheduleVote;
import com.example.meet.repository.BatchLogRepository;
import com.example.meet.repository.MeetRepository;
import com.example.meet.repository.ScheduleVoteRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

public class TerminateScheduleVote extends CommonJob {
    private final ScheduleVoteRepository scheduleVoteRepository;
    private final MeetRepository meetRepository;

    public TerminateScheduleVote(BatchLogRepository batchLogRepository, ScheduleVoteRepository scheduleVoteRepository,
            MeetRepository meetRepository) {
        super(batchLogRepository);
        this.scheduleVoteRepository = scheduleVoteRepository;
        this.meetRepository = meetRepository;
    }

    @Override
    @Transactional
    protected String performJob(JobExecutionContext context) {
        LocalDateTime currentDate = LocalDateTime.now();
        List<ScheduleVote> scheduleVoteList = scheduleVoteRepository.findEventsWithNullDateResultAndEndDateBefore(currentDate);
        StringBuilder log = new StringBuilder();

        log.append("[");

        for(ScheduleVote scheduleVote : scheduleVoteList){
            try {
                scheduleVote.setDateResult();
                scheduleVoteRepository.save(scheduleVote);
                Meet meet = scheduleVote.getMeet();
                meet.setDateResult(scheduleVote.getDateResult());
                meetRepository.save(meet);

                log.append(scheduleVote.getMeet().getTitle());
                log.append(", ");
            } catch (Exception e) {
                super.insertBatch("TerminateScheduleVote::"+scheduleVote.getId(), "FAILURE", e.getMessage());
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
}
