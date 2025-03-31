package com.example.snsbackend.common.aop;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Aspect
@Slf4j
@Component
public class LogAspect {
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    public Object logRequestDetails(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        String clientIp = request.getRemoteAddr();

        log.info("Request: [{}] {} {}", method, requestUri, request.getProtocol());
        log.info("from IP: {}", clientIp);

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed(); // 실제 메서드 실행
        long duration = System.currentTimeMillis() - startTime;

        log.info("Response: [{}] {} ({}ms)", method, requestUri, duration);

        return result;
    }
}
