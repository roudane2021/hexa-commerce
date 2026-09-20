package com.roudane.commerce.common.web.client;


import com.roudane.commerce.common.web.TraceHeaders;
import org.slf4j.MDC;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

public class TraceIdPropagationInterceptor implements ClientHttpRequestInterceptor {


    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        String traceId = MDC.get(TraceHeaders.MDC_TRACE_ID_KEY);
        if (traceId != null && !traceId.isBlank()) {
            request.getHeaders().set(TraceHeaders.TRACE_ID_HEADER, traceId);
        }
        return execution.execute(request, body);
    }
}
