package com.example.meet.common.exception;

import com.example.meet.common.CommonResponse;
import com.example.meet.common.utils.LoggerManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final LoggerManager loggerManager;

    @ExceptionHandler(BusinessException.class)
    protected CommonResponse<Void> handleBusinessException(HttpServletRequest request, BusinessException e) {
        loggerManager.logError(request, e);

        return CommonResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    protected CommonResponse<Void> handleException(HttpServletRequest request, Exception e) {
        loggerManager.logError(request, e);
        return CommonResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
    }
}
