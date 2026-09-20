package com.roudane.commerce.batch.job.cleanup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CleanupOldOrdersTasklet implements Tasklet {

    private static final Logger log = LoggerFactory.getLogger(CleanupOldOrdersTasklet.class);

    private final RestClient restClient;

    public CleanupOldOrdersTasklet(@Value("${order-service.base-url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        log.info("Nettoyage des anciennes commandes annulées — démarrage");

        // Exemple simplifié : appellerait un endpoint dédié /api/orders/archive-old
        // restClient.post().uri("/api/orders/archive-old").retrieve().toBodilessEntity();

        log.info("Nettoyage terminé");
        return RepeatStatus.FINISHED; // signale que la tâche est complète (pas de répétition)
    }
}
