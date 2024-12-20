package com.example.meet.common.exception;

import com.example.meet.common.CommonResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected CommonResponse<Void> handleBusinessException(HttpServletRequest request, BusinessException e) {
        return CommonResponse.fail(e.getErrorCode());
    }

    @ExceptionHandler(Exception.class)
    protected CommonResponse<Void> handleException(HttpServletRequest request, Exception e) {
        return CommonResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR,e.getMessage());
    }
}
