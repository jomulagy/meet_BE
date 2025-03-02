package com.example.meet.service;

import static java.lang.Long.parseLong;

import com.example.meet.common.dto.TemplateArgs;
import com.example.meet.common.dto.request.CreateMeetRequestDto;
import com.example.meet.common.dto.request.DeleteMeetRequestDto;
import com.example.meet.common.dto.request.EditMeetRequestDto;
import com.example.meet.common.dto.request.FindMeetRequestDto;
import com.example.meet.common.dto.response.meet.CreateMeetResponseDto;
import com.example.meet.common.dto.response.meet.EditMeetResponseDto;
import com.example.meet.common.dto.response.meet.FindMeetResponseDto;
import com.example.meet.common.dto.response.meet.FindMeetSimpleResponseDto;
import com.example.meet.common.enumulation.ErrorCode;
import com.example.meet.common.enumulation.MeetType;
import com.example.meet.common.enumulation.MemberPrevillege;
import com.example.meet.common.enumulation.Message;
import com.example.meet.common.exception.BusinessException;
import com.example.meet.common.utils.MessageManager;
import com.example.meet.common.utils.ScheduleManager;
import com.example.meet.entity.Meet;
import com.example.meet.entity.Member;
import com.example.meet.entity.ParticipateVote;
import com.example.meet.entity.ParticipateVoteItem;
import com.example.meet.entity.PlaceVote;
import com.example.meet.entity.PlaceVoteItem;
import com.example.meet.entity.ScheduleVote;
import com.example.meet.entity.ScheduleVoteItem;
import com.example.meet.mapper.MeetMapper;
import com.example.meet.repository.MeetRepository;
import com.example.meet.repository.MemberRepository;
import com.example.meet.repository.ParticipateVoteItemRepository;
import com.example.meet.repository.ParticipateVoteRepository;
import com.example.meet.repository.PlaceVoteItemRepository;
import com.example.meet.repository.PlaceVoteRepository;
import com.example.meet.repository.ScheduleVoteItemRepository;
import com.example.meet.repository.ScheduleVoteRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetService {
    private final MeetRepository meetRepository;
    private final MeetMapper meetMapper = MeetMapper.INSTANCE;
    private final MemberRepository memberRepository;
    private final ScheduleVoteRepository scheduleVoteRepository;
    private final ScheduleVoteItemRepository scheduleVoteItemRepository;
    private final PlaceVoteRepository placeVoteRepository;
    private final PlaceVoteItemRepository placeVoteItemRepository;
    private final ParticipateVoteRepository participateVoteRepository;
    private final ParticipateVoteItemRepository participateVoteItemRepository;

    private final MessageManager messageManager;

    @Transactional
    public CreateMeetResponseDto createMeet(CreateMeetRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인 (관리자, 멤버 여부)
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        Meet entity = meetMapper.dtoToEntity(inDto, user);

        if(inDto.getType() != MeetType.Routine){
            entity.setParticipantsNum(0);
        }
        meetRepository.save(entity);

        //일정 투표 연결
        if(entity.getScheduleVote() == null){
            ScheduleVote scheduleVote = createScheduleVote(entity);
            scheduleVoteRepository.save(scheduleVote);
            if(entity.getType() == MeetType.Routine){
                setScheduleVoteItems(scheduleVote);
            }

            entity.setScheduleVote(scheduleVote);
        }
        //장소 투표 연결
        if(entity.getPlaceVote() == null){
            PlaceVote placeVote = createPlaceVote(entity);
            placeVoteRepository.save(placeVote);
            setPlaceVoteItems(placeVote);
            entity.setPlaceVote(placeVote);
        }

        //참여 여부 투표 연결
        ParticipateVote participateVote = createParticipateVote(entity);
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
        LocalDate currentDate = LocalDate.now();
        LocalDate tomorrowDate = currentDate.plusDays(1);
        LocalDateTime endDate = tomorrowDate.atTime(LocalTime.of(0, 0));

        return ScheduleVote.builder()
                .endDate(endDate)
                .meet(meet)
                .build();
    }

    private PlaceVote createPlaceVote(Meet meet) {
        LocalDate currentDate = LocalDate.now();
        LocalDate tomorrowDate = currentDate.plusDays(2);
        LocalDateTime endDate = tomorrowDate.atTime(LocalTime.of(0, 0));

        return PlaceVote.builder()
                .endDate(endDate)
                .meet(meet)
                .build();
    }

    private ParticipateVote createParticipateVote(Meet meet) {
        LocalDate currentDate = LocalDate.now();
        LocalDate tomorrowDate = currentDate.plusDays(3);
        LocalDateTime endDate = tomorrowDate.atTime(LocalTime.of(0, 16));

        return ParticipateVote.builder()
                .totalNum(0)
                .endDate(endDate)
                .meet(meet)
                .build();
    }

    private void setScheduleVoteItems(ScheduleVote scheduleVote) {
        LocalDate today = LocalDate.now();

        // 다음 분기의 첫 번째 달 계산
        int nextQuarterStartMonth = ScheduleManager.calculateNextQuarterStartMonth(today.getMonthValue());
        int month = today.getMonthValue();
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

    private void setPlaceVoteItems(PlaceVote placeVote) {
        Member author = memberRepository.findById(Long.valueOf("2927398983")).orElseThrow();

        PlaceVoteItem placeVoteItem1 = PlaceVoteItem.builder()
                .place("강남역")
                .placeVote(placeVote)
                .editable(false)
                .author(author)
                .build();
        placeVoteItemRepository.save(placeVoteItem1);

        PlaceVoteItem placeVoteItem2 = PlaceVoteItem.builder()
                .place("종각역")
                .placeVote(placeVote)
                .editable(false)
                .author(author)
                .build();
        placeVoteItemRepository.save(placeVoteItem2);

        placeVote.getPlaceVoteItems().add(placeVoteItem1);
        placeVote.getPlaceVoteItems().add(placeVoteItem2);
        placeVoteRepository.save(placeVote);
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

    public List<FindMeetSimpleResponseDto> findMeetList(FindMeetRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인 (관리자, 멤버 여부)
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        //모임 조회
        List<Meet> meetList = meetRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return meetMapper.EntityListToDtoList(meetList);
    }

    public FindMeetResponseDto findMeet(FindMeetRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인 (관리자, 멤버 여부)
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        //모임 조회
        Meet meet = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        return meetMapper.EntityToDto(meet, user);
    }

    @Transactional
    public EditMeetResponseDto editMeet(EditMeetRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인 (관리자, 멤버 여부)
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        //모임 조회
        Meet meet = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        //작성자 or 관리자 인지 확인
        if(!(user.getPrevillege().equals(MemberPrevillege.admin) || meet.getAuthor() == user)){
            throw new BusinessException(ErrorCode.MEET_EDIT_PERMISSION_REQUIRED);
        }

        //투표한 필드는 편집 불가(날짜)
        if(meet.getDate() != null && meet.getScheduleVote() != null && meet.getScheduleVote().getDateResult() != null && !Objects.equals(inDto.getDate(), meet.getDate().toLocalDate())){
            throw new BusinessException(ErrorCode.VOTE_REQUIRED);
        }

        //투표한 필드는 편집 불가(시간)
        if(meet.getDate() != null && meet.getScheduleVote() != null && meet.getScheduleVote().getDateResult() != null && !Objects.equals(inDto.getTime(), meet.getDate().toLocalTime())){
            throw new BusinessException(ErrorCode.VOTE_REQUIRED);
        }

        //투표한 필드는 편집 불가(장소)
        if(meet.getPlace() != null && meet.getPlaceVote() != null && meet.getPlaceVote().getPlaceResult() != null && !Objects.equals(inDto.getPlace(), meet.getPlace())){
            throw new BusinessException(ErrorCode.VOTE_REQUIRED);
        }

        meet.update(inDto);

        return meetMapper.EntityToUpdateDto(meet);
    }

    public void deleteMeet(DeleteMeetRequestDto inDto) {
        //로그인 한 유저 확인
        Member user = memberRepository.findById(inDto.getUserId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEMBER_NOT_EXISTS)
        );

        //로그인 한 유저의 권한 확인 (관리자, 멤버 여부)
        if(user.getPrevillege().equals(MemberPrevillege.denied)){
            throw new BusinessException(ErrorCode.MEMBER_PERMISSION_REQUIRED);
        }

        //모임 조회
        Meet meet = meetRepository.findById(inDto.getMeetId()).orElseThrow(
                () -> new BusinessException(ErrorCode.MEET_NOT_EXISTS)
        );

        //작성자 or 관리자 인지 확인
        if(!(user.getPrevillege().equals(MemberPrevillege.admin) || meet.getAuthor() == user)){
            throw new BusinessException(ErrorCode.MEET_EDIT_PERMISSION_REQUIRED);
        }

        meetRepository.delete(meet);
    }
}
