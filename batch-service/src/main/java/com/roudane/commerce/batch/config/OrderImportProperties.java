package com.roudane.commerce.batch.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "batch.import")
public record OrderImportProperties(
        int chunkSize,
        int skipLimit,
        int retryLimit,
        long retryBackoffMs,
        String[] csvFields) {
}