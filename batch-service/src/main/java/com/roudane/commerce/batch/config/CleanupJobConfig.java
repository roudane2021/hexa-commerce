package com.roudane.commerce.batch.config;

import com.roudane.commerce.batch.job.cleanup.CleanupOldOrdersTasklet;
import com.roudane.commerce.batch.listener.JobLoggingListener;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class CleanupJobConfig {

    @Bean
    public Step cleanupStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            CleanupOldOrdersTasklet tasklet) {
        return new StepBuilder("cleanupStep", jobRepository)
                .tasklet(tasklet, transactionManager)
                .build();
    }

    @Bean
    public Job cleanupJob(JobRepository jobRepository,
                          @Qualifier("cleanupStep") Step cleanupStep, JobLoggingListener jobLoggingListener) {
        return new JobBuilder("cleanupJob", jobRepository)
                .listener(jobLoggingListener)
                .start(cleanupStep)
                .build();
    }
}