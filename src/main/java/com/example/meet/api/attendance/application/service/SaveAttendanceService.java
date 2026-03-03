package com.example.meet.api.attendance.application.service;

import com.example.meet.api.attendance.adapter.in.dto.in.SaveAttendanceRequestDto;
import com.example.meet.api.attendance.adapter.in.dto.out.SaveAttendanceResponseDto;
import com.example.meet.api.attendance.application.domain.entity.Attendance;
import com.example.meet.api.attendance.application.domain.entity.Penalty;
import com.example.meet.api.attendance.application.port.in.SaveAttendanceUseCase;
import com.example.meet.api.attendance.application.port.out.CreateAttendancePort;
import com.example.meet.api.attendance.application.port.out.CreatePenaltyPort;
import com.example.meet.api.attendance.application.port.out.GetAttendancePort;
import com.example.meet.api.attendance.application.port.out.GetPenaltyPort;
import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.api.member.application.port.out.GetMemberPort;
import com.example.meet.api.message.application.port.in.SendMessageUseCase;
import com.example.meet.api.post.application.domain.entity.Post;
import com.example.meet.api.post.application.port.in.GetPostUseCase;
import com.example.meet.batch.application.port.in.RegisterJobUseCase;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.exception.BusinessException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class SaveAttendanceService implements SaveAttendanceUseCase {
    private final GetAttendancePort getAttendancePort;
    private final CreateAttendancePort createAttendancePort;
    private final GetPenaltyPort getPenaltyPort;
    private final CreatePenaltyPort createPenaltyPort;
    private final GetPostUseCase getPostUseCase;
    private final GetMemberPort getMemberPort;
    private final SendMessageUseCase sendMessageUseCase;
    private final RegisterJobUseCase registerJobUseCase;

    @Override
    public SaveAttendanceResponseDto saveAttendance(SaveAttendanceRequestDto requestDto) {
        Post post = getPostUseCase.getEntity(requestDto.getPostId());

        List<String> penaltyNotifiedMembers = new ArrayList<>();

        for (Long memberId : requestDto.getAttendedMembers()) {
            Member member = getMemberPort.getMemberById(memberId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS));
            saveAttendanceRecord(post, member, true, penaltyNotifiedMembers);
        }

        for (Long memberId : requestDto.getAbsentMembers()) {
            Member member = getMemberPort.getMemberById(memberId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS));
            saveAttendanceRecord(post, member, false, penaltyNotifiedMembers);
        }

        return SaveAttendanceResponseDto.builder()
                .postId(post.getId())
                .totalAttended(requestDto.getAttendedMembers().size())
                .totalAbsent(requestDto.getAbsentMembers().size())
                .penaltyNotifiedMembers(penaltyNotifiedMembers)
                .build();
    }

    private void saveAttendanceRecord(Post post, Member member, boolean isAttended,
                                      List<String> penaltyNotifiedMembers) {
        Optional<Attendance> existingOpt = getAttendancePort.findByMemberAndPost(member, post);

        if (existingOpt.isPresent()) {
            Attendance existing = existingOpt.get();
            boolean wasAbsent = !existing.getIsAttended();
            int oldConsecutiveAbsences = existing.getConsecutiveAbsences();

            int newConsecutiveAbsences = calculateConsecutiveAbsences(member, isAttended, existing);
            existing.updateAttendance(isAttended, newConsecutiveAbsences);

            // 불참 -> 참석: 등록된 배치(벌금 확인) 취소
            if (wasAbsent && isAttended) {
                getPenaltyPort.findByMemberIdAndPostId(member.getId(), post.getId())
                        .filter(p -> Boolean.TRUE.equals(p.getBatchScheduled()))
                        .ifPresent(penalty -> {
                            registerJobUseCase.cancelCheckDepositStatus(
                                    member.getId(), post.getId(), penalty.getBatchScheduledDate());
                            penalty.cancelBatch();
                            createPenaltyPort.save(penalty);
                        });
            }
            // 참석 -> 불참 또는 불참 유지이고 새로 연속 3회 이상이 된 경우
            else if (!isAttended && oldConsecutiveAbsences < 3 && newConsecutiveAbsences >= 3) {
                createAndNotifyPenalty(member, post, newConsecutiveAbsences, penaltyNotifiedMembers);
            }

            createAttendancePort.save(existing);

        } else {
            int consecutiveAbsences = calculateConsecutiveAbsences(member, isAttended, null);

            Attendance attendance = Attendance.builder()
                    .post(post)
                    .member(member)
                    .attendanceDate(post.getMeetDate())
                    .isAttended(isAttended)
                    .consecutiveAbsences(consecutiveAbsences)
                    .build();

            if (consecutiveAbsences >= 3) {
                createAndNotifyPenalty(member, post, consecutiveAbsences, penaltyNotifiedMembers);
            }

            createAttendancePort.save(attendance);
        }
    }

    private void createAndNotifyPenalty(Member member, Post post, int consecutiveAbsences,
                                        List<String> penaltyNotifiedMembers) {
        LocalDate paymentDeadline = LocalDate.now().plusDays(7);
        LocalDate batchDate = paymentDeadline.plusDays(1);

        Penalty penalty = Penalty.builder()
                .member(member)
                .post(post)
                .consecutiveAbsences(consecutiveAbsences)
                .penaltyPaid(false)
                .paymentDeadline(paymentDeadline)
                .batchScheduled(true)
                .batchScheduledDate(batchDate)
                .build();
        createPenaltyPort.save(penalty);

        sendMessageUseCase.sendDepositPenalty(member, post.getTitle(), paymentDeadline.toString());
        registerJobUseCase.checkDepositStatus(
                member.getId(), post.getId(), post.getTitle(), member.getName(), batchDate);

        penaltyNotifiedMembers.add(member.getName());
    }

    private int calculateConsecutiveAbsences(Member member, boolean isAttended, Attendance currentAttendance) {
        if (isAttended) {
            return 0;
        }

        Optional<Attendance> latestAttendance;
        if (currentAttendance != null) {
            latestAttendance = getAttendancePort.findLatestByMemberExcluding(member, currentAttendance.getId());
        } else {
            latestAttendance = getAttendancePort.findLatestByMember(member);
        }

        if (latestAttendance.isEmpty()) {
            return 1;
        }

        Attendance latest = latestAttendance.get();
        if (latest.getIsAttended()) {
            return 1;
        }

        return latest.getConsecutiveAbsences() + 1;
    }
}
