package com.example.meet.batch.application.port.in;

import com.example.meet.batch.adapter.in.dto.in.BatchExecuteRequestDto;

public interface BatchExecutePort {
    Boolean execute(BatchExecuteRequestDto requestDto);
}
