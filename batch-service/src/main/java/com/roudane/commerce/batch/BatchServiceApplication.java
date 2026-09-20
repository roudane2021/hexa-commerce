package com.roudane.commerce.batch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = "com.roudane.commerce")
@EnableBatchProcessing
@EnableAspectJAutoProxy
public class BatchServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BatchServiceApplication.class, args);
    }
}
