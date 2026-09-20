package com.roudane.commerce.batch.listener;

import com.roudane.commerce.batch.job.importorder.beans.CreateOrderApiRequest;
import com.roudane.commerce.batch.job.importorder.beans.OrderImportRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.listener.SkipListener;
import org.springframework.stereotype.Component;

@Component
public class SkipLoggingListener implements SkipListener<OrderImportRecord, CreateOrderApiRequest> {

    private static final Logger log = LoggerFactory.getLogger(SkipLoggingListener.class);

    @Override
    public void onSkipInRead(Throwable t) {
        log.warn("Ligne ignorée en LECTURE : {}", t.getMessage());
    }

    @Override
    public void onSkipInProcess(OrderImportRecord item, Throwable t) {
        log.warn("Item ignoré en TRAITEMENT : userId={} — raison: {}", item.getUserId(), t.getMessage());
    }

    @Override
    public void onSkipInWrite(CreateOrderApiRequest item, Throwable t) {
        log.warn("Item ignoré en ÉCRITURE : {} — raison: {}", item, t.getMessage());
    }
}
