package com.example.meet.post.application.service;

import com.example.meet.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.entity.Member;
import com.example.meet.entity.ParticipateVote;
import com.example.meet.entity.ParticipateVoteItem;
import com.example.meet.infrastructure.dto.TemplateArgs;
import com.example.meet.infrastructure.enumulation.Message;
import com.example.meet.infrastructure.mapper.MeetMapper;
import com.example.meet.infrastructure.repository.*;
import com.example.meet.infrastructure.utils.MessageManager;
import com.example.meet.infrastructure.utils.ScheduleManager;
import com.example.meet.post.adapter.in.dto.CreateMeetRequestDto;
import com.example.meet.post.adapter.in.dto.CreateMeetResponseDto;
import com.example.meet.post.application.domain.entity.Post;
import com.example.meet.post.application.port.in.CreatePostUseCase;
import com.example.meet.post.application.port.out.CreatePostPort;
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
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreatePostService implements CreatePostUseCase {
    private final MeetMapper meetMapper = MeetMapper.INSTANCE;
    private final GetLogginedInfoUseCase getLogginedInfoUseCase;
    private final ParticipateVoteRepository participateVoteRepository;
    private final ParticipateVoteItemRepository participateVoteItemRepository;
    private final CreatePostPort createPostPort;
    private final CreateVotePort createVotePort;
    private final CreateVoteItemPort createVoteItemPort;

    private final MessageManager messageManager;

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAdminAccess(authentication)")
    public CreateMeetResponseDto createMeet(CreateMeetRequestDto inDto) {
        Member user = getLogginedInfoUseCase.get();

        LocalDate date = null;
        LocalTime time = null;

        List<Vote> voteList = new ArrayList<>();

        if(inDto.getDate() != null){
            date = LocalDate.parse(inDto.getDate(), DATE_FORMATTER);
        }
        if(inDto.getTime() != null){
            time = LocalTime.parse(inDto.getTime(), TIME_FORMATTER);
        }

        LocalDateTime dateTime = null;
        if (date != null && time != null) {
            dateTime = date.atTime(time);
        }

        //일정 투표 연결
        if(dateTime == null){
            Vote scheduleVote = createScheduleVote(inDto.getVoteDeadline());
            setScheduleVoteItems(scheduleVote);
            voteList.add(scheduleVote);
        }

        //장소 투표 연결
        if(inDto.getPlace() == null){
            Vote placeVote = createPlaceVote();
            voteList.add(placeVote);
        }

        //참여 여부 투표 연결
        ParticipateVote participateVote = createParticipateVote(inDto.getParticipationDeadline());
        setParticiapteVoteItems(participateVote);

        Post post = Post.builder()
                .title(inDto.getTitle())
                .date(dateTime)
                .place(inDto.getPlace())
                .content(inDto.getContent())
                .author(user)
                .voteList(voteList)
                .participateVote(participateVote)
                .build();

        createPostPort.create(post);

        TemplateArgs templateArgs = TemplateArgs.builder()
                .title(post.getTitle())
                .but(post.getId().toString())
                .scheduleType(null)
                .build();

        Message.SCHEDULE.setTemplateArgs(templateArgs);
        messageManager.sendAll(Message.SCHEDULE).block();
        messageManager.sendMe(Message.SCHEDULE).block();

        return meetMapper.entityToCreateDto(post);
    }

    private Vote createScheduleVote(String voteDeadline) {
        LocalDateTime deadLine =
                LocalDate.parse(voteDeadline)
                        .atTime(23, 59, 59);

        return Vote.builder()
                .activeYn(true)
                .endDate(deadLine)
                .build();
    }

    private Vote createPlaceVote() {
        return Vote.builder()
                .build();
    }

    private ParticipateVote createParticipateVote(String participationDeadline) {
        DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime endDate = LocalDate.parse(participationDeadline, DATE_FORMATTER).atTime(23,59);

        return ParticipateVote.builder()
                .totalNum(0)
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
            scheduleVote.getVoteItems().add(scheduleVoteItem);
        }
    }

    private void setParticiapteVoteItems(ParticipateVote participateVote) {
        ParticipateVoteItem participateVoteItem1 = ParticipateVoteItem.builder()
                .isParticipate(true)
                .participateVote(participateVote)
                .editable(false)
                .build();

        ParticipateVoteItem participateVoteItem2 = ParticipateVoteItem.builder()
                .isParticipate(false)
                .participateVote(participateVote)
                .editable(false)
                .build();

        participateVote.getParticipateVoteItems().add(participateVoteItem1);
        participateVote.getParticipateVoteItems().add(participateVoteItem2);
    }
}
