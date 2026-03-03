package com.example.meet.api.attendance.adapter.out;

import com.example.meet.api.attendance.application.domain.entity.Penalty;
import com.example.meet.api.attendance.application.port.out.CreatePenaltyPort;
import com.example.meet.infrastructure.repository.PenaltyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CreatePenaltyAdapter implements CreatePenaltyPort {
    private final PenaltyRepository penaltyRepository;

    @Override
    public Penalty save(Penalty penalty) {
        return penaltyRepository.save(penalty);
    }
}
