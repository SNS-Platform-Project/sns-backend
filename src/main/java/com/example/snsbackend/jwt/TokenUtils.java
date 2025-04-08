package com.example.snsbackend.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TokenUtils {
    public static String extractAccessTokenFromHeader(HttpServletRequest request) {
        return extractJwt(request.getHeader("Authorization"));

    }

    public static String extractJwt(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        } else {
            return null;
        }
    }
    public static String hashString(String input) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(input);
    }
}
