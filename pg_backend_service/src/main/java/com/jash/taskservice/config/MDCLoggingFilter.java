package com.jash.taskservice.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class MDCLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            
            // Extract core routing metadata details for development tracking
            String ipAddress = httpRequest.getHeader("X-Forwarded-For");
            if (ipAddress == null || ipAddress.isEmpty()) {
                ipAddress = request.getRemoteAddr();
            }
            
            // Bind context fields directly to the logger thread context (MDC)
            MDC.put("clientIp", ipAddress);
            MDC.put("requestUrl", httpRequest.getRequestURI());
            MDC.put("httpMethod", httpRequest.getMethod());
        }

        try {
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw e;
        } finally {
            // Clean up the thread trace context memory safely
            MDC.clear();
        }
    }
}