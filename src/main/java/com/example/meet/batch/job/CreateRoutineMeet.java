package com.example.meet.batch.job;

import static java.lang.Long.parseLong;

import com.example.meet.batch.CommonJob;
import com.example.meet.infrastructure.dto.request.CreateMeetRequestDto;
import com.example.meet.infrastructure.enumulation.MeetType;
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
    protected String performJob(JobExecutionContext context) {
        LocalDate date = LocalDate.now();
        List<Integer> quarterList = List.of(2, 3, 4, 1);
        int year = date.getYear();
        int month = date.getMonthValue();
        int quarter = quarterList.get((month - 1) / 3);

        // 1분기면 내년
        if(quarter == 1){
            year+=1;
        }

        String title = String.format("%d년도 %d 분기 정기 회식", year, quarter);

        CreateMeetRequestDto inDto = CreateMeetRequestDto.builder()
                .userId(parseLong("2927398983"))
                .title(title)
                .type(MeetType.Routine)
                .build();

        meetService.createMeet(inDto);

        return title + " 생성 완료";
    }
}
