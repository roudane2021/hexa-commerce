package com.roudane.commerce.payment.domain.port.out;


import com.roudane.commerce.payment.domain.model.Payment;

public interface PaymentPersistencePort {

    /**
     * Sauvegarde le Payment ET enregistre l'événement à publier dans l'Outbox,
     * de façon ATOMIQUE (même transaction DB). Garantit qu'il est impossible
     * d'avoir un Payment sauvegardé sans son événement correspondant, ou l'inverse.
     *
     * @param payment       l'agrégat à persister (déjà muté : settle()/fail() déjà appelé)
     * @param topic         le topic Kafka cible (voir EventTopics dans common-messaging)
     * @param eventPayload  le JSON déjà sérialisé de l'événement à publier
     * @return le Payment tel que persisté
     */
    Payment saveAndEnqueueEvent(Payment payment, String topic, String eventPayload);
}