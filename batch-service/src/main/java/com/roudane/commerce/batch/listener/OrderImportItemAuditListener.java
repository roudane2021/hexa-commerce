package com.roudane.commerce.batch.listener;

import com.roudane.commerce.batch.job.importorder.beans.CreateOrderApiRequest;
import com.roudane.commerce.batch.job.importorder.beans.OrderImportRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.annotation.AfterProcess;
import org.springframework.batch.core.annotation.AfterWrite;
import org.springframework.batch.core.annotation.OnProcessError;
import org.springframework.batch.core.annotation.OnWriteError;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.stereotype.Component;


@Component
public class OrderImportItemAuditListener {

    private static final Logger log = LoggerFactory.getLogger(OrderImportItemAuditListener.class);

    @AfterProcess
    public void afterProcess(OrderImportRecord item, CreateOrderApiRequest result) {
        if (result == null) {
            log.warn("[AUDIT] Élément filtré par le processor : userId={}", item.getUserId());
        }
    }

    @OnProcessError
    public void onProcessError(OrderImportRecord item, Exception ex) {
        log.error("[AUDIT] Erreur durant le processing de la commande userId={} : {}",
                item.getUserId(), ex.getMessage());
    }

    @AfterWrite
    public void afterWrite(Chunk<? extends CreateOrderApiRequest> items) {
        log.info("[AUDIT] Lot de {} commandes transmises avec succès à l'API.", items.size());
    }

    @OnWriteError
    public void onWriteError(Exception exception, Chunk<? extends CreateOrderApiRequest> items) {
        log.error("[AUDIT] Échec lors de l'écriture du lot de {} éléments : {}",
                items.size(), exception.getMessage());
    }
}
