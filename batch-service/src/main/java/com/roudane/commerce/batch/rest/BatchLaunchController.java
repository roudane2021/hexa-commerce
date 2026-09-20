package com.roudane.commerce.batch.rest;


import com.roudane.commerce.common.annotation.LogTechnicalCall;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class BatchLaunchController {

    private final JobOperator jobOperator;
    private final Job orderImportJob;
    private final Job cleanupJob;
    //private final JobLauncher jobLauncher;

    public BatchLaunchController(JobOperator jobOperator,
                                 @Qualifier("orderImportJob") Job orderImportJob,
                                 @Qualifier("cleanupJob") Job cleanupJob) {
        this.jobOperator = jobOperator;
        this.orderImportJob = orderImportJob;
        this.cleanupJob = cleanupJob;
    }

    @PostMapping("/import-orders")
    @LogTechnicalCall("Controller : lancer batch")
    public ResponseEntity<String> launchImport(
            @RequestParam(name = "filePath", required = false,
                    defaultValue = "classpath:sample-data/orders-import.csv") String filePath
    ) throws Exception {

        final JobParameters jobParameters = new JobParametersBuilder()
                //.addLong("timestamp", System.currentTimeMillis())
                .addString("filePath", filePath)
                .toJobParameters();

        JobExecution execution = jobOperator.start(orderImportJob, jobParameters);

        return ResponseEntity.ok(
                "Job 'orderImportJob' lancé avec l'ID d'exécution : "
                        + execution.getId() + " - Status : " + execution.getStatus());
    }

    @PostMapping("/cleanup")
    public ResponseEntity<String> launchCleanup() throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution execution = jobOperator.start(cleanupJob, jobParameters);

        return ResponseEntity.ok(
                "Job 'orderImportJob' lancé avec l'ID d'exécution : "
                        + execution.getId() + " - Status : " + execution.getStatus());
    }
}
