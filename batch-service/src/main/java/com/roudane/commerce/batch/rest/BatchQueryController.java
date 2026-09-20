package com.roudane.commerce.batch.rest;


import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/batch/query")
public class BatchQueryController {

    private final JobRepository jobRepository;

    public BatchQueryController(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @GetMapping("/jobs/{jobName}/last-execution")
    public JobExecution lastExecution(@PathVariable(name = "jobName") String jobName) {
        return jobRepository.getLastJobExecution(jobName, new org.springframework.batch.core.job.parameters.JobParameters());
    }

    @GetMapping("/jobs/{jobName}/instances")
    public List<?> instances(@PathVariable(name = "jobName") String jobName) {
        return jobRepository.getJobInstances(jobName, 0, 20);
    }

    @GetMapping("/executions/{executionId}")
    public JobExecution executionById(@PathVariable(name = "executionId") Long executionId) {
        return jobRepository.getJobExecution(executionId);
    }
}