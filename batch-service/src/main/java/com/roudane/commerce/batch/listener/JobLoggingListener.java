package com.roudane.commerce.batch.listener;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobLoggingListener implements JobExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(JobLoggingListener.class);

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("Démarrage du job {} avec paramètres {}",
                jobExecution.getJobInstance().getJobName(), jobExecution.getJobParameters());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("Job {} terminé avec statut {} — durée: {}ms",
                jobExecution.getJobInstance().getJobName(),
                jobExecution.getStatus(),
                jobExecution.getEndTime() != null && jobExecution.getStartTime() != null
                        ? java.time.Duration.between(jobExecution.getStartTime(), jobExecution.getEndTime()).toMillis()
                        : -1);

        jobExecution.getStepExecutions().forEach(step ->
                log.info("  Step '{}' : lus={} traités(skipped)={} écrits={} commits={}",
                        step.getStepName(), step.getReadCount(), step.getSkipCount(),
                        step.getWriteCount(), step.getCommitCount())
        );
    }
}
