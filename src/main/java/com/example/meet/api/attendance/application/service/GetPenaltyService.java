package com.example.meet.api.attendance.application.service;

import com.example.meet.api.attendance.adapter.in.dto.out.PenaltyResponseDto;
import com.example.meet.api.attendance.application.domain.entity.Penalty;
import com.example.meet.api.attendance.application.port.in.GetPenaltyUseCase;
import com.example.meet.api.attendance.application.port.out.GetPenaltyPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetPenaltyService implements GetPenaltyUseCase {
    private final GetPenaltyPort getPenaltyPort;

    @Override
    public PenaltyResponseDto getAllMembersPenaltyStatus() {
        List<Penalty> latestPenalties = getPenaltyPort.findLatestForAllMembers();

        List<PenaltyResponseDto.PenaltyMemberInfo> memberInfos = latestPenalties.stream()
                .map(penalty -> PenaltyResponseDto.PenaltyMemberInfo.builder()
                        .memberId(penalty.getMember().getId())
                        .memberName(penalty.getMember().getName())
                        .consecutiveAbsences(penalty.getConsecutiveAbsences())
                        .penaltyPaid(penalty.getPenaltyPaid())
                        .paymentDeadline(penalty.getPaymentDeadline())
                        .build())
                .collect(Collectors.toList());

        return PenaltyResponseDto.builder()
                .members(memberInfos)
                .build();
    }
}
