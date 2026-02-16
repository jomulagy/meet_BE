package com.example.meet.batch.application.service;

import com.example.meet.batch.adapter.in.dto.in.BatchExecuteRequestDto;
import com.example.meet.batch.application.port.in.BatchExecutePort;
import com.example.meet.batch.application.domain.entity.BatchLog;
import com.example.meet.infrastructure.repository.BatchLogRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BatchExecuteService implements BatchExecutePort {
    private static final String JOB_PACKAGE = "com.example.meet.batch.job";

    private final BatchLogRepository batchLogRepository;
    private final Scheduler scheduler;

    @Override
    public Boolean execute(BatchExecuteRequestDto requestDto) {
        try {
            Class<? extends Job> job = resolveJobClass(requestDto.getName());

            JobBuilder jobBuilder = JobBuilder.newJob(job)
                    .withIdentity(requestDto.getName() + "_web");

            if (requestDto.getParams() != null) {
                requestDto.getParams().forEach(jobBuilder::usingJobData);
            }

            JobDetail jobDetail = jobBuilder.build();

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

    @SuppressWarnings("unchecked")
    private Class<? extends Job> resolveJobClass(String name) throws ClassNotFoundException {
        Class<?> clazz = Class.forName(JOB_PACKAGE + "." + name);
        if (!Job.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(name + " is not a Job class");
        }
        return (Class<? extends Job>) clazz;
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
