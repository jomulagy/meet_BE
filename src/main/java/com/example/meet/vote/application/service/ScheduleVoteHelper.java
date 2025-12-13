package com.example.meet.vote.application.service;

import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.meet.application.port.out.GetMeetPort;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.port.out.GetVotePort;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduleVoteHelper {
    private final GetMeetPort getMeetPort;
    private final GetVotePort getVotePort;

    public Meet getMeet(Long meetId) {
        return getMeetPort.getMeetById(meetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEET_NOT_EXISTS));
    }

    public Vote getVote(Meet meet) {
        return getVotePort.getByMeetId(meet.getId()).orElse(null);
    }

    public void validateVoteIsActive(Meet meet) {
        LocalDateTime endDate = meet.getEndDate();
        if (endDate != null && endDate.isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.SCHEDULE_VOTE_END);
        }
    }
}
