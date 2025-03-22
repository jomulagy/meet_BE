package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.entity.Meet;
import com.example.meet.entity.PlaceVote;
import com.example.meet.repository.BatchLogRepository;
import com.example.meet.repository.MeetRepository;
import com.example.meet.repository.PlaceVoteRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

public class TerminatePlaceVote extends CommonJob {
    private final PlaceVoteRepository placeVoteRepository;
    private final MeetRepository meetRepository;

    public TerminatePlaceVote(BatchLogRepository batchLogRepository, PlaceVoteRepository placeVoteRepository,
            MeetRepository meetRepository) {
        super(batchLogRepository);
        this.placeVoteRepository = placeVoteRepository;
        this.meetRepository = meetRepository;
    }

    @Override
    @Transactional
    protected String performJob(JobExecutionContext context) {
        LocalDateTime currentDate = LocalDateTime.now();
        List<PlaceVote> placeVoteList = placeVoteRepository.findEventsWithNullDateResultAndEndDateBefore(currentDate);
        StringBuilder log = new StringBuilder();

        log.append("[");

        for(PlaceVote placeVote : placeVoteList) {
            try {
                placeVote.setPlaceResult();
                placeVoteRepository.save(placeVote);

                Meet meet = placeVote.getMeet();
                meet.setPlace();
                meetRepository.save(meet);

                log.append(placeVote.getMeet().getTitle());
                log.append(", ");
            } catch (Exception e) {
                super.insertBatch("TerminatePlaceVote::"+placeVote.getId(), "FAILURE", e.getMessage());
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
