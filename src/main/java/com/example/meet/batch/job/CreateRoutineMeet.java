package com.example.meet.batch.job;

import static java.lang.Long.parseLong;

import com.example.meet.batch.CommonJob;
import com.example.meet.api.post.adapter.in.dto.CreateMeetRequestDto;
import com.example.meet.api.post.application.port.in.CreatePostUseCase;
import com.example.meet.infrastructure.repository.BatchLogRepository;
import java.time.LocalDate;
import java.util.List;
import org.quartz.JobExecutionContext;

public class CreateRoutineMeet extends CommonJob {
    private final CreatePostUseCase createPostUseCase;

    public CreateRoutineMeet(BatchLogRepository batchLogRepository, CreatePostUseCase createPostUseCase) {
        super(batchLogRepository);
        this.createPostUseCase = createPostUseCase;
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
                .title(title)
                .build();

        createPostUseCase.createMeet(inDto);

        return title + " 생성 완료";
    }
}
