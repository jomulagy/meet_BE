package com.example.meet.meet.application.service;

import com.example.meet.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.entity.Member;
import com.example.meet.entity.ParticipateVote;
import com.example.meet.entity.ParticipateVoteItem;
import com.example.meet.entity.PlaceVote;
import com.example.meet.infrastructure.dto.TemplateArgs;
import com.example.meet.infrastructure.enumulation.Message;
import com.example.meet.infrastructure.mapper.MeetMapper;
import com.example.meet.infrastructure.repository.*;
import com.example.meet.infrastructure.utils.MessageManager;
import com.example.meet.infrastructure.utils.ScheduleManager;
import com.example.meet.meet.adapter.in.dto.CreateMeetRequestDto;
import com.example.meet.meet.adapter.in.dto.CreateMeetResponseDto;
import com.example.meet.meet.application.domain.entity.Meet;
import com.example.meet.meet.application.port.in.CreateMeetUseCase;
import com.example.meet.vote.application.domain.entity.Vote;
import com.example.meet.vote.application.domain.entity.VoteItem;
import com.example.meet.vote.application.port.out.CreateVoteItemPort;
import com.example.meet.vote.application.port.out.CreateVotePort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateMeetService implements CreateMeetUseCase {
    private final MeetMapper meetMapper = MeetMapper.INSTANCE;
    private final GetLogginedInfoUseCase getLogginedInfoUseCase;
    private final MeetRepository meetRepository;
    private final PlaceVoteRepository placeVoteRepository;
    private final ParticipateVoteRepository participateVoteRepository;
    private final ParticipateVoteItemRepository participateVoteItemRepository;
    private final CreateVotePort createVotePort;
    private final CreateVoteItemPort createVoteItemPort;

    private final MessageManager messageManager;

    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAdminAccess(authentication)")
    public CreateMeetResponseDto create(CreateMeetRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();

        Meet meet = meetMapper.dtoToEntity(inDto, user);

        meetRepository.save(meet);

        //일정 투표 연결
        if(meet.getScheduleVote() == null && meet.getDate() == null){
            Vote scheduleVote = createScheduleVote(meet, inDto.getVoteDeadline());
            setScheduleVoteItems(scheduleVote);
        }

        //장소 투표 연결
        if(meet.getPlaceVote() == null && meet.getPlace() == null){
            PlaceVote placeVote = createPlaceVote(meet);
            placeVoteRepository.save(placeVote);
        }

        //참여 여부 투표 연결
        ParticipateVote participateVote = createParticipateVote(meet, inDto.getParticipationDeadline());
        participateVoteRepository.save(participateVote);

        setParticiapteVoteItems(participateVote);
        meet.setParticipateVote(participateVote);

        meetRepository.save(meet);

        TemplateArgs templateArgs = TemplateArgs.builder()
                .title(meet.getTitle())
                .but(meet.getId().toString())
                .scheduleType(null)
                .build();

        Message.SCHEDULE.setTemplateArgs(templateArgs);
        messageManager.sendAll(Message.SCHEDULE).block();
        messageManager.sendMe(Message.SCHEDULE).block();

        return meetMapper.entityToCreateDto(meet);
    }

    private Vote createScheduleVote(Meet meet, String voteDeadline) {
        LocalDateTime deadLine =
                LocalDate.parse(voteDeadline)
                        .atTime(23, 59, 59);

        Vote vote =  Vote.builder()
                .meet(meet)
                .activeYn(true)
                .endDate(deadLine)
                .build();

        return createVotePort.create(vote);
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

    private void setScheduleVoteItems(Vote scheduleVote) {
        LocalDate today = LocalDate.now();

        // 다음 달 계산
        int nextMonth = today.getMonthValue() + 1;
        if (nextMonth > 12) {
            nextMonth = 1;
            today = today.plusYears(1);
        }

        // 해당 월의 첫 번째 날과 마지막 날 가져오기
        YearMonth nextQuarterMonth = YearMonth.of(today.getYear(), nextMonth);
        LocalDate firstDayOfNextQuarterMonth = nextQuarterMonth.atDay(1);
        LocalDate lastDayOfNextQuarterMonth = nextQuarterMonth.atEndOfMonth();

        // 해당 월의 모든 금요일과 토요일 계산
        List<LocalDateTime> fridaysAndSaturdays = ScheduleManager.getFridaysAndSaturdays(firstDayOfNextQuarterMonth, lastDayOfNextQuarterMonth);

        for(LocalDateTime date : fridaysAndSaturdays){
            VoteItem scheduleVoteItem = VoteItem.builder().dateTime(date).vote(scheduleVote).editable(false).build();
            createVoteItemPort.create(scheduleVoteItem);
            scheduleVote.getVoteItems().add(scheduleVoteItem);
        }
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
