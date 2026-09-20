package com.roudane.commerce.common.outbox;

import com.roudane.commerce.common.outbox.entities.OutboxEventJpaEntity;
import com.roudane.commerce.common.outbox.repository.OutboxEventJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.List;

/**
 * Template Method : l'algorithme de polling/retry/idempotence est figé ici (final),
 * seule la façon de PUBLIER concrètement l'événement (Kafka, Rabbit, HTTP webhook...)
 * est déléguée aux sous-classes via doPublish().
 */
public abstract class AbstractOutboxEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(AbstractOutboxEventPublisher.class);

    private final OutboxEventJpaRepository outboxRepository;
    private final int maxRetry;

    protected AbstractOutboxEventPublisher(OutboxEventJpaRepository outboxRepository, int maxRetry) {
        this.outboxRepository = outboxRepository;
        this.maxRetry = maxRetry;
    }

    // Le squelette de l'algorithme — figé, non redéfinissable par les sous-classes.
    // @Scheduled est hérité automatiquement par Spring sur les sous-classes concrètes,
    // pas besoin de le redéclarer dans chaque service.
    @Scheduled(fixedDelayString = "${outbox.polling-interval-ms}")
    public  void publishPendingEvents() {
        List<OutboxEventJpaEntity> pending = outboxRepository.findTop50ByPublishedFalseOrderByCreatedAtAsc();

        for (OutboxEventJpaEntity event : pending) {
            if (event.getRetryCount() >= maxRetry) {
                onMaxRetryExceeded(event);
                continue;
            }

            try {
                doPublish(event); // ← LE SEUL POINT DE VARIATION, implémenté par service
                event.markPublished();
                outboxRepository.save(event);
                onPublishSuccess(event);

            } catch (Exception ex) {
                event.incrementRetry();
                outboxRepository.save(event);
                onPublishFailure(event, ex);
            }
        }
    }

    // Hook obligatoire : chaque service décide COMMENT il publie réellement
    protected abstract void doPublish(OutboxEventJpaEntity event) throws Exception;

    // Hooks optionnels avec comportement par défaut, redéfinissables si besoin
    protected void onPublishSuccess(OutboxEventJpaEntity event) {
        log.debug("Événement outbox {} publié sur {}", event.getId(), event.getTopic());
    }

    protected void onPublishFailure(OutboxEventJpaEntity event, Exception ex) {
        log.warn("Échec publication événement outbox {} (tentative {}/{}) : {}",
                event.getId(), event.getRetryCount(), maxRetry, ex.getMessage());
    }

    protected void onMaxRetryExceeded(OutboxEventJpaEntity event) {
        log.error("Événement outbox {} abandonné après {} tentatives — intervention manuelle requise",
                event.getId(), maxRetry);
    }
}
