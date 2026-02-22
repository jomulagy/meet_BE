package com.example.meet.api.attendance.application.service;

import com.example.meet.api.attendance.adapter.in.dto.in.SaveAttendanceRequestDto;
import com.example.meet.api.attendance.adapter.in.dto.out.SaveAttendanceResponseDto;
import com.example.meet.api.attendance.application.domain.entity.Attendance;
import com.example.meet.api.attendance.application.port.in.SaveAttendanceUseCase;
import com.example.meet.api.attendance.application.port.out.CreateAttendancePort;
import com.example.meet.api.attendance.application.port.out.GetAttendancePort;
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
    private final GetPostUseCase getPostUseCase;
    private final GetMemberPort getMemberPort;
    private final SendMessageUseCase sendMessageUseCase;
    private final RegisterJobUseCase registerJobUseCase;

    @Override
    public SaveAttendanceResponseDto saveAttendance(SaveAttendanceRequestDto requestDto) {
        // 1. Post 조회
        Post post = getPostUseCase.getEntity(requestDto.getPostId());

        List<String> penaltyNotifiedMembers = new ArrayList<>();

        // 2. 참석 회원 처리
        for (Long memberId : requestDto.getAttendedMembers()) {
            Member member = getMemberPort.getMemberById(memberId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS));

            saveAttendanceRecord(post, member, true, penaltyNotifiedMembers);
        }

        // 3. 불참 회원 처리
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
        // 기존 출석 기록 조회
        Optional<Attendance> existingOpt = getAttendancePort.findByMemberAndPost(member, post);

        if (existingOpt.isPresent()) {
            // 기존 데이터가 있으면 업데이트
            Attendance existing = existingOpt.get();
            boolean wasAbsent = !existing.getIsAttended();
            int oldConsecutiveAbsences = existing.getConsecutiveAbsences();
            LocalDate checkDate = LocalDate.now().plusDays(8); // 7일 + 1

            // 새로운 연속 불참 횟수 계산
            int newConsecutiveAbsences = calculateConsecutiveAbsences(member, isAttended, existing);

            // 출석 기록 업데이트
            existing.updateAttendance(isAttended, newConsecutiveAbsences);

            // 불참 -> 참석으로 변경되고, 배치가 등록되어 있는 경우 배치 취소
            if (wasAbsent && isAttended && existing.getBatchScheduled() != null && existing.getBatchScheduled()) {
                registerJobUseCase.cancelCheckDepositStatus(
                        member.getId(),
                        post.getId(),
                        existing.getBatchScheduledDate()
                );
                existing.cancelBatch();
            }
            // 참석 -> 불참으로 변경되고, 새로 연속 3회 이상 불참이 된 경우 배치 등록
            else if (!wasAbsent && !isAttended && newConsecutiveAbsences >= 3) {
                handleThreeConsecutiveAbsences(member, post);
                existing.scheduleBatch(checkDate);
                penaltyNotifiedMembers.add(member.getName());

                sendMessageUseCase.sendDepositPenalty(member, post.getTitle(), LocalDate.now().plusDays(7).toString());

            }
            // 불참 유지이고 새로 3회 이상이 된 경우 (이전에는 3회 미만)
            else if (wasAbsent && !isAttended && oldConsecutiveAbsences < 3 && newConsecutiveAbsences >= 3) {
                handleThreeConsecutiveAbsences(member, post);
                existing.scheduleBatch(checkDate);
                penaltyNotifiedMembers.add(member.getName());

                sendMessageUseCase.sendDepositPenalty(member, post.getTitle(), LocalDate.now().plusDays(7).toString());

            }

            createAttendancePort.save(existing);


        } else {
            // 기존 데이터가 없으면 새로 생성
            int consecutiveAbsences = calculateConsecutiveAbsences(member, isAttended, null);

            Attendance attendance = Attendance.builder()
                    .post(post)
                    .member(member)
                    .attendanceDate(post.getMeetDate())
                    .isAttended(isAttended)
                    .consecutiveAbsences(consecutiveAbsences)
                    .penaltyPaid(false)
                    .batchScheduled(false)
                    .build();

            // 연속 3회 이상 불참 시 처리
            if (consecutiveAbsences >= 3) {
                LocalDate checkDate = LocalDate.now().plusDays(7);
                handleThreeConsecutiveAbsences(member, post);
                attendance.scheduleBatch(checkDate);
                penaltyNotifiedMembers.add(member.getName());
            }

            createAttendancePort.save(attendance);
        }
    }

    private int calculateConsecutiveAbsences(Member member, boolean isAttended, Attendance currentAttendance) {
        if (isAttended) {
            return 0; // 참석하면 리셋
        }

        // 현재 기록을 제외한 가장 최근 출석 기록 조회
        Optional<Attendance> latestAttendance;

        if (currentAttendance != null) {
            // 업데이트인 경우: 현재 기록 제외하고 조회
            latestAttendance = getAttendancePort.findLatestByMemberExcluding(member, currentAttendance.getId());
        } else {
            // 새로 생성인 경우: 단순히 가장 최근 기록 조회
            latestAttendance = getAttendancePort.findLatestByMember(member);
        }

        if (latestAttendance.isEmpty()) {
            return 1; // 첫 불참 또는 이전 기록 없음
        }

        Attendance latest = latestAttendance.get();
        if (latest.getIsAttended()) {
            return 1; // 이전에 참석했으면 연속성 끊김, 1부터 시작
        }

        // 이전 기록도 불참이면 연속 불참 횟수 증가
        return latest.getConsecutiveAbsences() + 1;
    }

    private void handleThreeConsecutiveAbsences(Member member, Post post) {
        // 1. 회원에게 벌금 입금 메시지 전송 (개인 전송)
        LocalDate checkDate = LocalDate.now().plusDays(7);
        String deadline = checkDate.toString(); // yyyy-MM-dd 형식
        sendMessageUseCase.sendDepositPenalty(member, post.getTitle(), deadline);

        // 2. 7일 후 관리자에게 입금 확인 메시지 배치 작업 등록
        registerJobUseCase.checkDepositStatus(
                member.getId(),
                post.getId(),
                post.getTitle(),
                member.getName(),
                checkDate
        );
    }
}
