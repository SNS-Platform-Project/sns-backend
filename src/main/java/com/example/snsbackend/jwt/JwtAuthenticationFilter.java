package com.example.snsbackend.jwt;

import com.example.snsbackend.dto.ApiResponse;
import com.example.snsbackend.exception.ApiErrorType;
import com.example.snsbackend.exception.ApiException;
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
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtProvider jwtProvider;
    private static final List<String> WHITE_LIST = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/email/verify",
            "/api/v1/auth/email/verify-request",
            "/api/v1/users/check-username",
            "/api/v1/users/check-email",
            "/api/v1/auth/reset-password",
            "/api/v1/auth/refresh",
            "/portfolio"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            // Request Header에서 토큰, URI 추출
            String accessToken = TokenUtils.extractAccessTokenFromHeader((HttpServletRequest) request);
            String requestURI = ((HttpServletRequest) request).getRequestURI();

            // White List URL은 토큰 겁사 없이 통과
            if (WHITE_LIST.stream().anyMatch(requestURI::startsWith)) {
                chain.doFilter(request, response);
                return;
            }

            // Access Token 유효성 검사
            if (accessToken != null && jwtProvider.validateToken(accessToken)) {
                // SecurityContext에 유저 인증 정보 제공
                Authentication authentication = jwtProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else if (!jwtProvider.validateToken(accessToken)) {
                setResponse((HttpServletResponse) response,
                        new ApiResponse<>(HttpStatus.UNAUTHORIZED, "Blacklisted Access Token.", null));
                return;
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
