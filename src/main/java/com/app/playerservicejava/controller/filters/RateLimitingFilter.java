package com.app.playerservicejava.controller.filters;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BandwidthBuilder;
import io.github.bucket4j.Bucket;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter implements Filter {

    private Map<String, Bucket> bucketPerIp;

    private Bucket createBucket() {
        Bandwidth bw = BandwidthBuilder.builder()
                .capacity(100)
                .refillGreedy(100, Duration.ofSeconds(60))
                .initialTokens(100)
                .build();
        return Bucket.builder().addLimit(bw).build();
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
        bucketPerIp = new ConcurrentHashMap<>();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        String ip = servletRequest.getRemoteAddr();
        Bucket bucket = bucketPerIp.computeIfAbsent(ip, k -> createBucket());

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            httpResponse.getWriter().write("Allowed 100 requests per min");
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
