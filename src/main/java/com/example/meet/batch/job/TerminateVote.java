package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.entity.PlaceVote;
import com.example.meet.entity.ScheduleVote;
import com.example.meet.meet.domain.entity.Meet;
import com.example.meet.meet.port.out.GetMeetEndDateBeforePort;
import com.example.meet.repository.BatchLogRepository;
import com.example.meet.repository.MeetRepository;
import com.example.meet.repository.PlaceVoteRepository;
import com.example.meet.repository.ScheduleVoteRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

public class TerminateVote extends CommonJob {
    private final ScheduleVoteRepository scheduleVoteRepository;
    private final PlaceVoteRepository placeVoteRepository;
    private final MeetRepository meetRepository;
    private final GetMeetEndDateBeforePort getMeetEndDateBeforePort;

    public TerminateVote(BatchLogRepository batchLogRepository, ScheduleVoteRepository scheduleVoteRepository,
            PlaceVoteRepository placeVoteRepository, MeetRepository meetRepository,
            GetMeetEndDateBeforePort getMeetEndDateBeforePort) {
        super(batchLogRepository);
        this.scheduleVoteRepository = scheduleVoteRepository;
        this.placeVoteRepository = placeVoteRepository;
        this.meetRepository = meetRepository;
        this.getMeetEndDateBeforePort = getMeetEndDateBeforePort;
    }

    @Override
    @Transactional
    protected String performJob(JobExecutionContext context) {
        LocalDateTime currentDate = LocalDateTime.now();
        List<Meet> meetList = getMeetEndDateBeforePort.get(currentDate);
        StringBuilder log = new StringBuilder();

        log.append("[");

        for(Meet meet : meetList){
            try {

                ScheduleVote scheduleVote = meet.getScheduleVote();

                scheduleVote.setDateResult();
                scheduleVoteRepository.save(scheduleVote);
                meet.setDateResult(scheduleVote.getDateResult());

                meetRepository.save(meet);

                log.append("ScheduleVote :: " + scheduleVote.getMeet().getTitle());
                log.append(", ");
            } catch (Exception e) {
                super.insertBatch("Fail to terminate ScheduleVote :: " + meet.getId(), "FAILURE", e.getMessage());
            }

            try {
                PlaceVote placeVote = meet.getPlaceVote();

                placeVote.setPlaceResult();
                placeVoteRepository.save(placeVote);

                meet.setPlace();
                meetRepository.save(meet);

                log.append("PlaceVote :: " + placeVote.getMeet().getTitle());
                log.append(", ");
            } catch (Exception e) {
                super.insertBatch("Fail to terminate PlaceVote :: " + meet.getId(), "FAILURE", e.getMessage());
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
