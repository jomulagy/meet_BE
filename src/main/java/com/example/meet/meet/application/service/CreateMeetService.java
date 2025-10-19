package com.example.meet.meet.application.service;

import com.example.meet.entity.Member;
import com.example.meet.entity.ParticipateVote;
import com.example.meet.entity.ParticipateVoteItem;
import com.example.meet.entity.PlaceVote;
import com.example.meet.entity.ScheduleVote;
import com.example.meet.entity.ScheduleVoteItem;
import com.example.meet.infrastructure.dto.TemplateArgs;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.MemberPrevillege;
import com.example.meet.infrastructure.enumulation.Message;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.mapper.MeetMapper;
import com.example.meet.infrastructure.utils.MessageManager;
import com.example.meet.infrastructure.utils.ScheduleManager;
import com.example.meet.meet.adapter.in.dto.CreateMeetRequestDto;
import com.example.meet.meet.adapter.in.dto.CreateMeetResponseDto;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.meet.application.port.in.CreateMeetUseCase;
import com.example.meet.repository.MeetRepository;
import com.example.meet.repository.MemberRepository;
import com.example.meet.repository.ParticipateVoteItemRepository;
import com.example.meet.repository.ParticipateVoteRepository;
import com.example.meet.repository.PlaceVoteRepository;
import com.example.meet.repository.ScheduleVoteItemRepository;
import com.example.meet.repository.ScheduleVoteRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreateMeetService implements CreateMeetUseCase {
    private final MeetMapper meetMapper = MeetMapper.INSTANCE;
    private final MemberRepository memberRepository;
    private final MeetRepository meetRepository;
    private final ScheduleVoteRepository scheduleVoteRepository;
    private final ScheduleVoteItemRepository scheduleVoteItemRepository;
    private final PlaceVoteRepository placeVoteRepository;
    private final ParticipateVoteRepository participateVoteRepository;
    private final ParticipateVoteItemRepository participateVoteItemRepository;

    private final MessageManager messageManager;

    @Transactional
    public CreateMeetResponseDto create(CreateMeetRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인 (관리자, 멤버 여부)
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        Meet entity = meetMapper.dtoToEntity(inDto, user);

        meetRepository.save(entity);

        //일정 투표 연결
        if(entity.getScheduleVote() == null && entity.getDate() == null){
            ScheduleVote scheduleVote = createScheduleVote(entity);
            scheduleVoteRepository.save(scheduleVote);
            setScheduleVoteItems(scheduleVote);

            entity.setScheduleVote(scheduleVote);
        }

        //장소 투표 연결
        if(entity.getPlaceVote() == null && entity.getPlace() == null){
            PlaceVote placeVote = createPlaceVote(entity);
            placeVoteRepository.save(placeVote);
        }

        //참여 여부 투표 연결
        ParticipateVote participateVote = createParticipateVote(entity, inDto.getParticipationDeadline());
        participateVoteRepository.save(participateVote);

        setParticiapteVoteItems(participateVote);
        entity.setParticipateVote(participateVote);

        meetRepository.save(entity);

        TemplateArgs templateArgs = TemplateArgs.builder()
                .title(entity.getTitle())
                .but(entity.getId().toString())
                .scheduleType(null)
                .build();

        Message.SCHEDULE.setTemplateArgs(templateArgs);
        messageManager.sendAll(Message.SCHEDULE).block();
        messageManager.sendMe(Message.SCHEDULE).block();

        return meetMapper.entityToCreateDto(entity);
    }

    private ScheduleVote createScheduleVote(Meet meet) {
        return ScheduleVote.builder()
                .meet(meet)
                .build();
    }

    private PlaceVote createPlaceVote(Meet meet) {
        return PlaceVote.builder()
                .meet(meet)
                .build();
    }

    private ParticipateVote createParticipateVote(Meet meet, String participationDeadline) {
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime endDate = LocalDate.parse(participationDeadline, DATE_FORMATTER).atTime(23,59);

        return ParticipateVote.builder()
                .totalNum(0)
                .meet(meet)
                .endDate(endDate)
                .build();
    }

    private void setScheduleVoteItems(ScheduleVote scheduleVote) {
        LocalDate today = LocalDate.now();

        // 다음 분기의 첫 번째 달 계산
        int nextQuarterStartMonth = ScheduleManager.calculateNextQuarterStartMonth(today.getMonthValue());
        if(nextQuarterStartMonth < today.getMonthValue()){
            today = today.plusYears(1);
        }

        // 해당 월의 첫 번째 날과 마지막 날 가져오기
        YearMonth nextQuarterMonth = YearMonth.of(today.getYear(), nextQuarterStartMonth);
        LocalDate firstDayOfNextQuarterMonth = nextQuarterMonth.atDay(1);
        LocalDate lastDayOfNextQuarterMonth = nextQuarterMonth.atEndOfMonth();

        // 해당 월의 모든 금요일과 토요일 계산
        List<LocalDateTime> fridaysAndSaturdays = ScheduleManager.getFridaysAndSaturdays(firstDayOfNextQuarterMonth, lastDayOfNextQuarterMonth);

        for(LocalDateTime date : fridaysAndSaturdays){
            ScheduleVoteItem scheduleVoteItem = ScheduleVoteItem.builder().date(date).scheduleVote(scheduleVote).editable(false).build();
            scheduleVoteItemRepository.save(scheduleVoteItem);
            scheduleVote.getScheduleVoteItems().add(scheduleVoteItem);
        }
        scheduleVoteRepository.save(scheduleVote);

    }

    private void setParticiapteVoteItems(ParticipateVote participateVote) {
        ParticipateVoteItem participateVoteItem1 = ParticipateVoteItem.builder()
                .isParticipate(true)
                .participateVote(participateVote)
                .editable(false)
                .build();

        participateVoteItemRepository.save(participateVoteItem1);

        ParticipateVoteItem participateVoteItem2 = ParticipateVoteItem.builder()
                .isParticipate(false)
                .participateVote(participateVote)
                .editable(false)
                .build();

        participateVoteItemRepository.save(participateVoteItem2);

        participateVote.getParticipateVoteItems().add(participateVoteItem1);
        participateVote.getParticipateVoteItems().add(participateVoteItem2);

        participateVoteRepository.save(participateVote);
    }
}
