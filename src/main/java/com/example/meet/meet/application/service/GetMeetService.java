package com.example.meet.meet.application.service;

import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.meet.application.port.in.GetMeetUseCase;
import com.example.meet.meet.application.port.out.GetMeetPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GetMeetService implements GetMeetUseCase {

    private final GetMeetPort getMeetPort;

    @Override
    public Meet get(Long meetId) {
        return getMeetPort.getMeetById(meetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEET_NOT_EXISTS));
    }
}
