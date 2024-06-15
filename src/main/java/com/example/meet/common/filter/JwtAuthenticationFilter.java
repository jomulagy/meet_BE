package com.example.meet.common.filter;

import com.example.meet.common.CommonResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.meet.common.auth.JwtTokenProvider;
import com.example.meet.common.exception.BusinessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // /auth/** 경로에 대한 요청을 직접 허용
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();



        try{
            // 1. Request Header에서 JWT 토큰 추출
            String token = resolveToken((HttpServletRequest) request);

            // 2. validateToken으로 토큰 유효성 검사
            if (token != null && jwtTokenProvider.validateToken(token)) {
                // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

            chain.doFilter(request, response);
        } catch (BusinessException e) {
            handleException(response, e);
        }
        
    }

    private void handleException(ServletResponse response, BusinessException e) throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();
        if (response instanceof ServletResponse) {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("Content-Type", "application/json; charset=UTF-8");
            httpResponse.setStatus(e.getErrorCode().getHttpStatus().value());
            httpResponse.setCharacterEncoding("UTF-8");


            String jsonResponse = objectMapper.writeValueAsString(CommonResponse.fail(e.getErrorCode()));
            httpResponse.getWriter().write(jsonResponse);
            httpResponse.getWriter().flush();
        } else {
            throw new ServletException("Expected HttpServletResponse but got " + response.getClass().getName());
        }
    }

    // Request Header에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

