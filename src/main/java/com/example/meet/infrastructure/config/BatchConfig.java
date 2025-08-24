package com.example.meet.infrastructure.config;

import com.example.meet.batch.ScheduledJob;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BatchConfig {

    @Autowired
    private Scheduler scheduler;

    private static final int MAX_RETRIES = 3;  // 최대 재시도 횟수
    private static final long RETRY_DELAY_MS = 5000; // 재시도 간격 (5초)

    @PostConstruct
    public void scheduleJobs() {
        for (ScheduledJob scheduledJob : ScheduledJob.values()) {
            scheduleJob(scheduledJob);
        }
    }

    private void scheduleJob(ScheduledJob job) {
        int retries = 0;
        boolean success = false;

        while (retries < MAX_RETRIES && !success) {
            try {
                // Job 등록 및 Trigger 설정
                JobDetail jobDetail = JobBuilder.newJob(job.getJobClass())
                        .withIdentity(job.getJobClass().getSimpleName())
                        .build();

                Trigger trigger = TriggerBuilder.newTrigger()
                        .forJob(jobDetail)
                        .withIdentity(job.getJobClass().getSimpleName() + "Trigger")
                        .withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExpression()))
                        .build();

                scheduler.scheduleJob(jobDetail, trigger);
                success = true; // 성공적으로 작업이 등록되면 success로 설정
            } catch (SchedulerException e) {
                retries++;
                if (retries < MAX_RETRIES) {
                    try {
                        TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS); // 재시도 대기
                    } catch (InterruptedException interruptedException) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
    }
}
