package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.api.participate.adapter.in.dto.in.TerminateParticipateVoteRequestDto;
import com.example.meet.api.participate.application.port.in.UpdateParticipateVoteUseCase;
import com.example.meet.api.participate.application.domain.entity.ParticipateVote;
import com.example.meet.infrastructure.repository.BatchLogRepository;
import com.example.meet.infrastructure.repository.MeetRepository;
import com.example.meet.infrastructure.repository.ParticipateVoteRepository;

import java.util.List;

import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;

public class TerminateParticipateVote extends CommonJob {
    private final ParticipateVoteRepository participateVoteRepository;
    private final UpdateParticipateVoteUseCase updateParticipateVoteUseCase;
    private final MeetRepository meetRepository;

    @Autowired
    public TerminateParticipateVote(BatchLogRepository batchLogRepository, ParticipateVoteRepository participateVoteRepository, UpdateParticipateVoteUseCase updateParticipateVoteUseCase,
                                    MeetRepository meetRepository) {
        super(batchLogRepository);
        this.participateVoteRepository = participateVoteRepository;
        this.updateParticipateVoteUseCase = updateParticipateVoteUseCase;
        this.meetRepository = meetRepository;
    }

    @Override
    protected String performJob(JobExecutionContext context) {
        List<ParticipateVote> participateVoteList = participateVoteRepository.findByEndDateToday();
        StringBuilder log = new StringBuilder();

        log.append("[");

        for(ParticipateVote participateVote : participateVoteList){
            TerminateParticipateVoteRequestDto inDto = TerminateParticipateVoteRequestDto.builder()
                    .postId(participateVote.getPost().getId())
                    .build();

            updateParticipateVoteUseCase.terminate(inDto);

            log.append(participateVote.getPost().getId());
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
