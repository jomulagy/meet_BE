package com.example.meet.batch.job;

import static java.lang.Long.parseLong;

import com.example.meet.batch.CommonJob;
import com.example.meet.meet.adapter.in.dto.CreateMeetRequestDto;
import com.example.meet.meet.application.port.in.CreateMeetUseCase;
import com.example.meet.repository.BatchLogRepository;
import java.time.LocalDate;
import java.util.List;
import org.quartz.JobExecutionContext;

public class CreateRoutineMeet extends CommonJob {
    private final CreateMeetUseCase createMeetUseCase;

    public CreateRoutineMeet(BatchLogRepository batchLogRepository, CreateMeetUseCase createMeetUseCase) {
        super(batchLogRepository);
        this.createMeetUseCase = createMeetUseCase;
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
                .build();

        createMeetUseCase.create(inDto);

        return title + " 생성 완료";
    }
}
