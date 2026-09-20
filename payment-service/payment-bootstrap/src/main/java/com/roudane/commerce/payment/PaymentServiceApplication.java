package com.roudane.commerce.payment;

import com.roudane.commerce.common.kafka.producer.KafkaProducerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.roudane.commerce")
@EnableJpaAuditing
@EnableAspectJAutoProxy
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.roudane.commerce")
@EntityScan(basePackages = {
        "com.roudane.commerce.payment",
        "com.roudane.commerce.common"
})
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }
}
