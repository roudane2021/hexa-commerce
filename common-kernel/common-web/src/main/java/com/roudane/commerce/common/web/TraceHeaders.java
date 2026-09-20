package com.roudane.commerce.common.web;

import java.util.regex.Pattern;

public final class TraceHeaders {
    public static final String TRACE_ID_HEADER = "X-Trace-Id";
    public static final String MDC_TRACE_ID_KEY = "traceId";
    // Regex autorisant uniquement les UUID standards ou une chaîne alphanumérique avec tirets (8 à 64 caractères)
    public static final Pattern VALID_TRACE_ID_PATTERN = Pattern.compile("^[a-zA-Z0-9-]{8,64}$");

    private TraceHeaders() {
    }
}