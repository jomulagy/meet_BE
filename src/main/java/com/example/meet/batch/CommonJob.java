package com.example.meet.batch;


import com.example.meet.entity.BatchLog;
import com.example.meet.infrastructure.repository.BatchLogRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public abstract class CommonJob implements Job {
    protected final BatchLogRepository batchLogRepository;

    @Override
    public void execute(JobExecutionContext context){
        String jobName = context.getJobDetail().getKey().getName();

        try {
            // 자식 클래스의 실제 로직을 실행
            String rtnMsg = performJob(context);

            // 작업 성공 시 로깅
            insertBatch(jobName, "SUCCESS", rtnMsg);
        } catch (Exception e) {
            // 작업 실패 시 로깅
            insertBatch(jobName, "FAILURE", e.getMessage());
        }
    }

    protected abstract String performJob(JobExecutionContext context);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void insertBatch(String name, String status, String message){
        BatchLog log = new BatchLog();
        log.setName(name);
        log.setStatus(status);
        log.setMessage(message);
        log.setStartDate(LocalDateTime.now());
        batchLogRepository.save(log);
    }
}
