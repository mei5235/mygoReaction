package com.example.mygoReaction.interceptor;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Bucket bucket;

    public RateLimitInterceptor() {
        Bandwidth limit = Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1)));
        this.bucket = Bucket.builder().addLimit(limit).build();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        log.info("preHandle生效");
        log.info(request.getHeader("X-FORWARDED-FOR"));
        if(!bucket.tryConsume(1)){
            log.error("Too many request. Suspend Service temporarily.");
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            try (OutputStream o = response.getOutputStream()){
                o.write("Too many request. Suspend Service temporarily.".getBytes(StandardCharsets.UTF_8));
            }
            return false;
        }
        return true;
    }
}
