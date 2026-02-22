package com.example.meet.api.attendance.adapter.in;

import com.example.meet.api.attendance.adapter.in.dto.in.SaveAttendanceRequestDto;
import com.example.meet.api.attendance.adapter.in.dto.out.GetAttendanceResponseDto;
import com.example.meet.api.attendance.adapter.in.dto.out.SaveAttendanceResponseDto;
import com.example.meet.api.attendance.application.port.in.GetAttendanceUseCase;
import com.example.meet.api.attendance.application.port.in.SaveAttendanceUseCase;
import com.example.meet.infrastructure.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
@Tag(name = "Attendance", description = "출석 체크 API")
public class AttendanceController {
    private final SaveAttendanceUseCase saveAttendanceUseCase;
    private final GetAttendanceUseCase getAttendanceUseCase;

    @PostMapping
    @Operation(summary = "출석 체크/수정", description = "모임 참석/불참 회원 기록 (기존 기록이 있으면 업데이트)")
    public CommonResponse<SaveAttendanceResponseDto> saveAttendance(
            @RequestBody SaveAttendanceRequestDto requestDto) {
        return CommonResponse.success(saveAttendanceUseCase.saveAttendance(requestDto));
    }

    @GetMapping("/{postId}")
    @Operation(summary = "출석 조회", description = "특정 모임의 출석 결과 조회")
    @Parameter(name = "postId", description = "모임 ID", example = "89")
    public CommonResponse<GetAttendanceResponseDto> getAttendance(
            @PathVariable(name = "postId") Long postId) {
        return CommonResponse.success(getAttendanceUseCase.getAttendanceByPostId(postId));
    }
}
