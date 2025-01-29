package com.example.meet.batch;

import java.util.ArrayList;
import java.util.List;

import com.example.meet.entity.BatchLog;
import com.example.meet.repository.BatchLogRepository;
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
    protected final List<String> taskList = new ArrayList<>();

    @Override
    public void execute(JobExecutionContext context){
        String jobName = context.getJobDetail().getKey().getName();

        try {
            // 자식 클래스의 실제 로직을 실행
            performJob(context);

            // 작업 성공 시 로깅
            insertBatch(jobName, "SUCCESS", taskList);
        } catch (Exception e) {
            // 작업 실패 시 로깅
            insertBatch(jobName, "FAILURE", e.getMessage());
        }
    }

    protected abstract void performJob(JobExecutionContext context);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void insertBatch(String name, String status, String message){

        BatchLog log = new BatchLog();
        log.setName(name);
        log.setStatus(status);
        log.setMessage(message);
        log.setStartDate(LocalDateTime.now());
        batchLogRepository.save(log);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void insertBatch(String name, String status, List<String> messages){
        String message = convertListToString(messages);

        BatchLog log = new BatchLog();
        log.setName(name);
        log.setStatus(status);
        log.setMessage(message);
        log.setStartDate(LocalDateTime.now());
        batchLogRepository.save(log);
    }

    private String convertListToString(List<String> list){
        if(list.isEmpty()){
            return "";
        }

        StringBuilder result = new StringBuilder();
        if(list.size() == 1){
            return list.get(0);
        }

        result.append("[");

        for(String str : list){
            result.append(str);
            result.append(", ");
        }

        // 마지막 ", " 제거
        int index = result.lastIndexOf( ", ");

        if (index != -1 && index == result.length() - 2) {
            result.delete(index, result.length());
        }

        result.append("]");

        return result.toString();
    }
}
