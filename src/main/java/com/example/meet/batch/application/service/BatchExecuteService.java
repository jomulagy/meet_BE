package com.example.meet.batch.application.service;

import com.example.meet.batch.ScheduledJob;
import com.example.meet.batch.adapter.in.dto.in.BatchExecuteRequestDto;
import com.example.meet.batch.application.port.in.BatchExecutePort;
import com.example.meet.entity.BatchLog;
import com.example.meet.infrastructure.repository.BatchLogRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BatchExecuteService implements BatchExecutePort {
    private final BatchLogRepository batchLogRepository;
    private final Scheduler scheduler;

    @Override
    public Boolean execute(BatchExecuteRequestDto requestDto) {
        try {
            Class<? extends Job> job = ScheduledJob.valueOf(toSnakeCase(requestDto.getName())).getJobClass();

            JobDetail jobDetail = JobBuilder.newJob(job)
                    .withIdentity(requestDto.getName() + "_web")
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .startNow()
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);
        } catch (Exception e) {
            insertBatch(requestDto.getName(), "FAILURE", e.getMessage());
            return false;
        }

        return true;
    }

    private String toSnakeCase(String str) {
        return str.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
    }

    private void insertBatch(String name, String status, String message){
        BatchLog log = new BatchLog();
        log.setName(name);
        log.setStatus(status);
        log.setMessage(message);
        log.setStartDate(LocalDateTime.now());
        batchLogRepository.save(log);
    }
}
