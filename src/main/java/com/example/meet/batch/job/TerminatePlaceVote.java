package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.entity.PlaceVote;
import com.example.meet.repository.BatchLogRepository;
import com.example.meet.repository.PlaceVoteRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.quartz.JobExecutionContext;
import org.springframework.transaction.annotation.Transactional;

public class TerminatePlaceVote extends CommonJob {
    private final PlaceVoteRepository placeVoteRepository;

    public TerminatePlaceVote(BatchLogRepository batchLogRepository, PlaceVoteRepository placeVoteRepository) {
        super(batchLogRepository);
        this.placeVoteRepository = placeVoteRepository;
    }

    @Override
    @Transactional
    protected void performJob(JobExecutionContext context) {
        LocalDateTime currentDate = LocalDateTime.now();
        List<PlaceVote> placeVoteList = placeVoteRepository.findEventsWithNullDateResultAndEndDateBefore(currentDate);

        for(PlaceVote placeVote : placeVoteList){
            placeVote.setPlaceResult();
            placeVote.getMeet().setPlaceResult(placeVote.getPlaceResult());

            taskList.add(placeVote.getMeet().getTitle());
        }
    }
}
