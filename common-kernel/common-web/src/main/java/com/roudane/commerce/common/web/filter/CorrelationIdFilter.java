package com.roudane.commerce.payment.config;

import com.roudane.commerce.common.web.TraceHeaders;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String traceId = resolveTraceId(request);
            MDC.put(TraceHeaders.MDC_TRACE_ID_KEY, traceId);
            response.setHeader(TraceHeaders.TRACE_ID_HEADER, traceId);

            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(TraceHeaders.MDC_TRACE_ID_KEY);
        }
    }

    private String resolveTraceId(HttpServletRequest request) {
        String incoming = request.getHeader(TraceHeaders.TRACE_ID_HEADER);

        return Optional.ofNullable(incoming)
                .map(String::strip)
                .filter(this::isValidTraceId)
                .orElseGet(() -> UUID.randomUUID().toString());
    }


    private boolean isValidTraceId(final String traceId) {

        return TraceHeaders.VALID_TRACE_ID_PATTERN.matcher(traceId.trim()).matches();
    }
}