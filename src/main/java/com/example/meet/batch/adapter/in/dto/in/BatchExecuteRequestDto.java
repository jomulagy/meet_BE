package com.example.meet.batch.adapter.in.dto.in;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchExecuteRequestDto {
    private String name;
    private Map<String, String> params;
}
