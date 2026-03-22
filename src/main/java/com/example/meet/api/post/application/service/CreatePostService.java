package com.example.meet.api.post.application.service;

import com.example.meet.api.auth.application.port.in.GetLogginedInfoUseCase;
import com.example.meet.batch.application.port.in.RegisterJobUseCase;
import com.example.meet.api.member.application.domain.entity.Member;
import com.example.meet.infrastructure.dto.TemplateArgs;
import com.example.meet.infrastructure.enumulation.ErrorCode;
import com.example.meet.infrastructure.enumulation.Message;
import com.example.meet.infrastructure.enumulation.VoteStatus;
import com.example.meet.infrastructure.enumulation.VoteType;
import com.example.meet.infrastructure.exception.BusinessException;
import com.example.meet.infrastructure.utils.MessageManager;
import com.example.meet.infrastructure.enumulation.PostType;
import com.example.meet.infrastructure.utils.ScheduleManager;
import com.example.meet.api.post.adapter.in.dto.CreateMeetRequestDto;
import com.example.meet.api.post.adapter.in.dto.CreateMeetResponseDto;
import com.example.meet.api.post.application.domain.entity.Post;
import com.example.meet.api.post.application.port.in.CreatePostUseCase;
import com.example.meet.api.post.application.port.out.CreatePostPort;
import com.example.meet.api.vote.application.domain.entity.Vote;
import com.example.meet.api.vote.application.domain.entity.VoteItem;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CreatePostService implements CreatePostUseCase {
    private final CreatePostPort createPostPort;
    private final RegisterJobUseCase registerJobUseCase;

    private final MessageManager messageManager;

    private final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Override
    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAdminAccess(authentication)")
    public CreateMeetResponseDto createMeet(CreateMeetRequestDto inDto) {
        Vote scheduleVote = null;
        Vote placeVote = null;

        LocalDate date = null;
        LocalTime time = null;
        YearMonth yearMonth = null;

        String dateStr = inDto.getDate();
        String timeStr = inDto.getTime();

        if (dateStr == null && timeStr == null) {
            // null case: 다음 분기 금/토 자동 생성
        } else if (dateStr != null && timeStr == null && dateStr.matches("\\d{4}-\\d{2}")) {
            // 년/월 case: 해당 월의 금/토 생성
            try {
                yearMonth = YearMonth.parse(dateStr, YEAR_MONTH_FORMATTER);
            } catch (DateTimeParseException e) {
                throw new BusinessException(ErrorCode.INVALID_DATE_FORMAT);
            }
        } else if (dateStr != null && timeStr != null && dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
            // 년/월/일/시간 case: 날짜 확정
            try {
                date = LocalDate.parse(dateStr, DATE_FORMATTER);
                time = LocalTime.parse(timeStr, TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                throw new BusinessException(ErrorCode.INVALID_DATE_FORMAT);
            }
        } else {
            throw new BusinessException(ErrorCode.INVALID_DATE_FORMAT);
        }

        LocalDateTime dateTime = null;
        if (date != null && time != null) {
            dateTime = date.atTime(time);
        }

        Post post = Post.builder()
                .title(inDto.getTitle())
                .content(inDto.getContent())
                .type(PostType.MEET)
                .status(VoteStatus.VOTE)
                .build();

        //일정 투표 연결
        if(dateTime == null){
            scheduleVote = createScheduleVote(inDto.getVoteDeadline());
            if (yearMonth != null) {
                setScheduleVoteItemsByYearMonth(scheduleVote, yearMonth);
            } else {
                setScheduleVoteItems(scheduleVote);
            }
            post.addVote(scheduleVote);
        }

        //장소 투표 연결
        if(inDto.getPlace() == null){
            placeVote = createPlaceVote(inDto.getVoteDeadline());
            post.addVote(placeVote);
        }

        Post saved = createPostPort.create(post);

        TemplateArgs templateArgs = TemplateArgs.builder()
                .title(saved.getTitle())
                .but(saved.getId().toString())
                .scheduleType(null)
                .build();

        if(scheduleVote != null) {
            registerJobUseCase.terminateVote(scheduleVote);
        }

        if(placeVote != null) {
            registerJobUseCase.terminateVote(placeVote);
        }

        Message.VOTE.setTemplateArgs(templateArgs);
        messageManager.sendAll(Message.VOTE).block();
        messageManager.sendMe(Message.VOTE).block();

        return CreateMeetResponseDto
                .builder()
                .id(post.getId())
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAdminAccess(authentication)")
    public CreateMeetResponseDto createNotification(CreateMeetRequestDto inDto) {
        Post post = Post.builder()
                .title(inDto.getTitle())
                .content(inDto.getContent())
                .type(PostType.NOTIFICATION)
                .status(VoteStatus.FINISHED)
                .build();

        Post saved = createPostPort.create(post);

        TemplateArgs templateArgs = TemplateArgs.builder()
                .title(saved.getTitle())
                .but(saved.getId().toString())
                .scheduleType(null)
                .build();

        Message.POST.setTemplateArgs(templateArgs);
        messageManager.sendAll(Message.POST).block();
        messageManager.sendMe(Message.POST).block();

        return CreateMeetResponseDto
                .builder()
                .id(post.getId())
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAdminAccess(authentication)")
    public CreateMeetResponseDto createVote(CreateMeetRequestDto inDto) {
        Post post = Post.builder()
                .title(inDto.getTitle())
                .content(inDto.getContent())
                .type(PostType.VOTE)
                .status(VoteStatus.VOTE)
                .build();

        Post saved = createPostPort.create(post);

        TemplateArgs templateArgs = TemplateArgs.builder()
                .title(saved.getTitle())
                .but(saved.getId().toString())
                .scheduleType(null)
                .build();

        Message.POST.setTemplateArgs(templateArgs);
        messageManager.sendAll(Message.POST).block();
        messageManager.sendMe(Message.POST).block();

        return CreateMeetResponseDto
                .builder()
                .id(post.getId())
                .build();
    }

    @Override
    @Transactional
    @PreAuthorize("@memberPermissionEvaluator.hasAdminAccess(authentication)")
    public CreateMeetResponseDto createTravel(CreateMeetRequestDto requestDto) {
        Post post = Post.builder()
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .type(PostType.TRAVEL)
                .status(VoteStatus.VOTE)
                .build();

        Post saved = createPostPort.create(post);

        TemplateArgs templateArgs = TemplateArgs.builder()
                .title(saved.getTitle())
                .but(saved.getId().toString())
                .scheduleType(null)
                .build();

        Message.POST.setTemplateArgs(templateArgs);
        messageManager.sendAll(Message.POST).block();
        messageManager.sendMe(Message.POST).block();

        return CreateMeetResponseDto
                .builder()
                .id(post.getId())
                .build();
    }

    private Vote createScheduleVote(String voteDeadline) {
        LocalDateTime deadLine =
                LocalDate.parse(voteDeadline)
                        .atTime(0, 0, 0);

        return Vote.builder()
                .title("날짜 투표")
                .endDate(deadLine)
                .activeYn(true)
                .type(VoteType.DATE)
                .isDuplicate(true)
                .build();
    }

    private Vote createPlaceVote(String voteDeadline) {
        LocalDateTime deadLine =
                LocalDate.parse(voteDeadline)
                        .atTime(0, 0, 0);

        return Vote.builder()
                .title("장소 투표")
                .activeYn(true)
                .endDate(deadLine)
                .type(VoteType.PLACE)
                .isDuplicate(true)
                .build();
    }

    private void setScheduleVoteItemsByYearMonth(Vote scheduleVote, YearMonth yearMonth) {
        LocalDate firstDay = yearMonth.atDay(1);
        LocalDate lastDay = yearMonth.atEndOfMonth();

        List<LocalDateTime> fridaysAndSaturdays = ScheduleManager.getFridaysAndSaturdays(firstDay, lastDay);

        for (LocalDateTime date : fridaysAndSaturdays) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            VoteItem scheduleVoteItem = VoteItem.builder().content(date.format(formatter)).vote(scheduleVote).build();
            scheduleVote.getVoteItems().add(scheduleVoteItem);
        }
    }

    private void setScheduleVoteItems(Vote scheduleVote) {
        LocalDate today = LocalDate.now();

        // 다음 달 계산
        int nextMonth = today.getMonthValue() + 3;
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
            DateTimeFormatter formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            VoteItem scheduleVoteItem = VoteItem.builder().content(date.format(formatter)).vote(scheduleVote).build();
            scheduleVote.getVoteItems().add(scheduleVoteItem);
        }
    }
}
