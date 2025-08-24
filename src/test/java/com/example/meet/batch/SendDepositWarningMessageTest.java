package com.example.meet.batch;

import static org.mockito.Mockito.mock;

import com.example.meet.MeetApplication;
import com.example.meet.batch.job.SendDepositWarningMessage;
import com.example.meet.infrastructure.utils.MessageManager;
import com.example.meet.repository.BatchLogRepository;
import com.example.meet.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.quartz.JobExecutionContext;

@SpringBootTest(classes = MeetApplication.class)
@ActiveProfiles("local")
public class SendDepositWarningMessageTest {

    @Autowired
    private CommonJob sendDepositWarningMessage;

    @Autowired
    private BatchLogRepository batchLogRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MessageManager messageManager;

    @Test
    void perform(){
        sendDepositWarningMessage = new SendDepositWarningMessage(batchLogRepository, memberRepository, messageManager);
        // JobExecutionContext Mock 생성
        JobExecutionContext jobExecutionContext = mock(JobExecutionContext.class);

        sendDepositWarningMessage.performJob(jobExecutionContext);
    }
}
