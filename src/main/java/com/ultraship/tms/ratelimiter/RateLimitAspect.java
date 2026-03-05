package com.ultraship.tms.ratelimiter;

import com.ultraship.tms.exception.ratelimit.RatelimitException;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class RateLimitAspect {

    private final RedisRateLimiterService rateLimiter;

    public RateLimitAspect(RedisRateLimiterService rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @Around("@annotation(rateLimit)")
    public Object rateLimit(
            ProceedingJoinPoint joinPoint,
            RateLimit rateLimit
    ) throws Throwable {

        ServletRequestAttributes attr =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        HttpServletRequest request = null;
        if (attr != null) {
            request = attr.getRequest();
        }

        String userId = SecurityUtil.getCurrentUserId();

        String identifier;

        if (userId != null) {
            identifier = "user:" + userId;
        } else {
            identifier = "ip:" + IpUtil.getClientIp(request);
        }

        String resolver = joinPoint.getSignature().getName();

        String key = "rate_limit:" + identifier + ":" + resolver;

        boolean allowed = rateLimiter.allowRequest(
                key,
                rateLimit.limit(),
                rateLimit.duration()
        );

        if (!allowed) {
            throw new RatelimitException("Too many requests");
        }

        return joinPoint.proceed();
    }
}