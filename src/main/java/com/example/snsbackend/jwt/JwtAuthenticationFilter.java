package com.example.snsbackend.jwt;

import com.example.snsbackend.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
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

        } catch (ExpiredJwtException e) {
            setResponse((HttpServletResponse) response,
                    new ApiResponse<>(HttpStatus.UNAUTHORIZED, "JWT token has expired", null));
        } catch (UnsupportedJwtException e) {
            setResponse((HttpServletResponse) response,
                    new ApiResponse<>(HttpStatus.BAD_REQUEST, "JWT token is unsupported", null));
        } catch (IllegalArgumentException e) {
            setResponse((HttpServletResponse) response,
                    new ApiResponse<>(HttpStatus.BAD_REQUEST, "JWT payload is invalid", null));
        } catch (SignatureException e) {
            setResponse((HttpServletResponse) response,
                    new ApiResponse<>(HttpStatus.UNAUTHORIZED, "JWT signature does not match locally computed signature", null));
        } catch (AuthenticationException e) {
            setResponse((HttpServletResponse) response,
                    new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Authentication failed. Invalid credentials.", null));
        } catch (JwtException e) {
            setResponse((HttpServletResponse) response,
                    new ApiResponse<>(HttpStatus.UNAUTHORIZED, "JWT was not correctly constructed", null));

        }
    }

    private void setResponse(HttpServletResponse response, ApiResponse<?> e) {
        response.setStatus(e.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try {
            log.warn(e.getMessage());
            response.getWriter().write(new ObjectMapper().writeValueAsString(e));
        } catch (IOException ioException) {
            log.error("Error writing response: " + ioException.getMessage());
        }
    }
}
