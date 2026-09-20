package com.roudane.commerce.batch.config;


import com.roudane.commerce.batch.job.importorder.OrderImportItemProcessor;
import com.roudane.commerce.batch.job.importorder.OrderImportItemWriter;
import com.roudane.commerce.batch.job.importorder.beans.CreateOrderApiRequest;
import com.roudane.commerce.batch.job.importorder.beans.OrderImportRecord;
import com.roudane.commerce.batch.listener.JobLoggingListener;
import com.roudane.commerce.batch.listener.OrderImportItemAuditListener;
import com.roudane.commerce.batch.listener.SkipLoggingListener;
import com.roudane.commerce.common.annotation.LogTechnicalCall;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.validator.ValidationException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.HttpServerErrorException;

@Configuration
@EnableConfigurationProperties(OrderImportProperties.class)
public class OrderImportJobConfig {

    public static final String JOB_NAME = "orderImportJob";
    public static final String STEP_NAME = "orderImportStep";

    private final OrderImportProperties properties;

    public OrderImportJobConfig(OrderImportProperties properties) {
        this.properties = properties;
    }

    @Bean
    @StepScope
    @LogTechnicalCall("Lecture batch commandes vers order-service")
    public FlatFileItemReader<OrderImportRecord> orderImportReader(
            @Value("#{jobParameters['filePath'] ?: 'classpath:sample-data/orders-import.csv'}") Resource resource) {
        return new FlatFileItemReaderBuilder<OrderImportRecord>()
                .name("orderImportReader")
                .resource(resource)
                .strict(true)
                .linesToSkip(1) // ignore l'en-tête CSV
                .delimited()
                .names(properties.csvFields())
                .targetType(OrderImportRecord.class)
                .build();
    }

    @Bean
    public Step orderImportStep(JobRepository jobRepository,
                                PlatformTransactionManager transactionManager,
                                FlatFileItemReader<OrderImportRecord> orderImportReader,
                                OrderImportItemProcessor orderImportItemProcessor,
                                OrderImportItemWriter orderImportItemWriter,
                                SkipLoggingListener skipLoggingListener,
                                OrderImportItemAuditListener itemAuditListener) {

        return new StepBuilder(STEP_NAME, jobRepository)
                .<OrderImportRecord, CreateOrderApiRequest>chunk(properties.chunkSize())
                .transactionManager(transactionManager)
                .reader(orderImportReader)
                .processor(orderImportItemProcessor)
                .writer(orderImportItemWriter)

                .faultTolerant()
                // Skip policy pour erreurs fonctionnelles / de format
                .skip(ValidationException.class)
                .skipLimit(properties.skipLimit())

                // Retry policy pour erreurs réseau/système avec délai de pause
                .retry(HttpServerErrorException.class)
                .retryLimit(properties.retryLimit())


                .listener(skipLoggingListener)
                .listener(itemAuditListener)
                .build();
    }

    @Bean
    public Job orderImportJob(JobRepository jobRepository,
                              @Qualifier("orderImportStep") Step orderImportStep,
                              JobLoggingListener jobLoggingListener) {

        return new JobBuilder(JOB_NAME, jobRepository)
                //.incrementer(new RunIdIncrementer())
                .listener(jobLoggingListener)
                .start(orderImportStep)
                .build();
    }



/*    @Bean
    public BackOffPolicy backOffPolicy() {
        FixedBackOffPolicy policy = new FixedBackOffPolicy();
        policy.setBackOffPeriod(properties.retryBackoffMs());
        return policy;
    }*/
}
