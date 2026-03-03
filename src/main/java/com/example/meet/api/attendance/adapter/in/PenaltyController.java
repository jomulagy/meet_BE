package com.example.meet.api.attendance.adapter.in;

import com.example.meet.api.attendance.adapter.in.dto.in.UpdatePenaltyRequestDto;
import com.example.meet.api.attendance.adapter.in.dto.out.PenaltyResponseDto;
import com.example.meet.api.attendance.application.port.in.GetPenaltyUseCase;
import com.example.meet.api.attendance.application.port.in.UpdatePenaltyUseCase;
import com.example.meet.infrastructure.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/penalty")
@Tag(name = "Penalty", description = "벌금 관리 API")
public class PenaltyController {
    private final GetPenaltyUseCase getPenaltyUseCase;
    private final UpdatePenaltyUseCase updatePenaltyUseCase;

    @GetMapping
    @Operation(summary = "벌금 대상자 조회", description = "모든 회원의 연속 불참 횟수 및 벌금 납부 여부 조회")
    public CommonResponse<PenaltyResponseDto> getPenaltyStatus() {
        return CommonResponse.success(getPenaltyUseCase.getAllMembersPenaltyStatus());
    }

    @PutMapping("/{memberId}")
    @Operation(summary = "벌금 입금 여부 업데이트", description = "특정 회원의 벌금 납부 상태 업데이트")
    @Parameter(name = "memberId", description = "회원 ID", example = "123")
    public CommonResponse<Void> updatePenaltyStatus(
            @PathVariable(name = "memberId") Long memberId,
            @RequestBody UpdatePenaltyRequestDto requestDto) {
        updatePenaltyUseCase.updatePenaltyStatus(memberId, requestDto);
        return CommonResponse.success(null);
    }
}
