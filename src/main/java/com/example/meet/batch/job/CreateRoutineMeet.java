package com.example.meet.batch.job;

import static java.lang.Long.parseLong;

import com.example.meet.batch.CommonJob;
import com.example.meet.common.dto.request.CreateMeetRequestDto;
import com.example.meet.common.enumulation.MeetType;
import com.example.meet.repository.BatchLogRepository;
import com.example.meet.service.MeetService;
import java.time.LocalDate;
import java.util.List;
import org.quartz.JobExecutionContext;

public class CreateRoutineMeet extends CommonJob {
    private final MeetService meetService;

    public CreateRoutineMeet(BatchLogRepository batchLogRepository, MeetService meetService) {
        super(batchLogRepository);
        this.meetService = meetService;
    }

    @Override
    protected void performJob(JobExecutionContext context) {
        LocalDate date = LocalDate.now();
        List<Integer> quarterList = List.of(1, 2, 3, 4);
        int year = date.getYear();
        int month = date.getMonthValue();
        int quarter = quarterList.get((month - 1) / 3);

        String title = String.format("%d년도 %d 분기 정기 회식", year, quarter);

        CreateMeetRequestDto inDto = CreateMeetRequestDto.builder()
                .userId(parseLong("2927398983"))
                .title(title)
                .type(MeetType.Routine)
                .build();

        meetService.createMeet(inDto);

        taskList.add(title + " 생성 완료");
    }
}
