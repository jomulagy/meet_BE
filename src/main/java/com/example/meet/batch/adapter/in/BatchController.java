package com.example.meet.batch.adapter.in;

import com.example.meet.batch.adapter.in.dto.in.BatchExecuteRequestDto;
import com.example.meet.batch.application.port.in.BatchExecutePort;
import com.example.meet.infrastructure.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
@RequiredArgsConstructor
public class BatchController {
    private final BatchExecutePort batchExecutePort;

    // 즉시 실행 트리거
    @PostMapping("/execute")
    public CommonResponse<Boolean> execute(@RequestBody BatchExecuteRequestDto requestDto) {
        Boolean result = batchExecutePort.execute(requestDto);
        return CommonResponse.success(result);
    }
}
