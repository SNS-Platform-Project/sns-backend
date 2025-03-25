package com.example.snsbackend.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            // Request Header에서 토큰 추출
            String token = TokenUtils.extractAccessTokenFromHeader((HttpServletRequest) request);
            // 토큰 유효성 검사
            if (token != null && jwtProvider.validateToken(token)) {
                // SecurityContext에 유저 인증 정보 제공
                Authentication authentication = jwtProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            chain.doFilter(request, response);
        } catch (IOException | ServletException e) {
            log.error("Exception during JWT filter processing", e);
            throw e;
        }
    }
}
