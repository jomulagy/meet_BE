package com.example.meet.batch.job;

import com.example.meet.batch.CommonJob;
import com.example.meet.batch.ScheduledJob;
import com.example.meet.entity.BatchLog;
import com.example.meet.infrastructure.repository.BatchLogRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Component;

@Component
public class RerunFailedJobs extends CommonJob {
    private final BatchLogRepository batchLogRepository;
    private final Scheduler scheduler;

    public RerunFailedJobs(BatchLogRepository batchLogRepository, BatchLogRepository batchLogRepository1,
            Scheduler scheduler) {
        super(batchLogRepository);
        this.batchLogRepository = batchLogRepository1;
        this.scheduler = scheduler;
    }

    @Override
    public String performJob(JobExecutionContext context){
        StringBuilder log = new StringBuilder();

        log.append("[");

        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        List<BatchLog> failedJobs = batchLogRepository.findByStatusAndStartDateAfter("FAILURE", yesterday);

        for (BatchLog batchLog : failedJobs) {
            try {
                Class<? extends Job> job = ScheduledJob.valueOf(toSnakeCase(batchLog.getName())).getJobClass();

                JobDetail jobDetail = JobBuilder.newJob(job)
                        .withIdentity(batchLog.getName() + "_retry")
                        .build();

                Trigger trigger = TriggerBuilder.newTrigger()
                        .startNow()
                        .build();

                log.append(batchLog.getName());
                log.append(", ");

                scheduler.scheduleJob(jobDetail, trigger);
            } catch (Exception e) {
                super.insertBatch(batchLog.getName(), "FAILURE", e.getMessage());
            }
        }

        // 마지막 ", " 제거
        int index = log.lastIndexOf( ", ");

        if (index != -1 && index == log.length() - 2) {
            log.delete(index, log.length());
        }

        log.append("]");

        return log.toString();
    }

    private String toSnakeCase(String str) {
        return str.replaceAll("([a-z])([A-Z])", "$1_$2").toUpperCase();
    }
}
