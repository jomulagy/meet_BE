package com.example.meet.common.exception;

import com.example.meet.common.CommonResponse;
import com.example.meet.common.variables.ErrorCode;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    protected CommonResponse<Void> handleBusinessException(BusinessException e) {
        return CommonResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    protected CommonResponse<Void> handleException(Exception e) {
        return CommonResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR);
    }
}
