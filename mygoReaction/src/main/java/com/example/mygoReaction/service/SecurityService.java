package com.example.mygoReaction.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
    /**
     * Returns client IP from which the request was made. {@code X-Forwarded-For} header is also checked in case the
     * service is located behind a load balancer.
     *
     * @return client IP.
     */
    public String getClientIP(HttpServletRequest request) {
        String xForwardedHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedHeader == null) {
            return request.getRemoteAddr();
        }
        return xForwardedHeader.split(",")[0];
    }
}