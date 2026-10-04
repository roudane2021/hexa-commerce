package com.roudane.commerce.order;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Arrays;

@SpringBootApplication(scanBasePackages = "com.roudane.commerce")
@EnableJpaAuditing
@EnableAspectJAutoProxy
@EnableScheduling
@EnableJpaRepositories(basePackages = "com.roudane.commerce")
@EntityScan(basePackages = {
        "com.roudane.commerce.order",
        "com.roudane.commerce.common"
})
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }

    @Bean
    ApplicationRunner debug(ApplicationContext ctx) {
        return args -> {
            Arrays.stream(ctx.getBeanDefinitionNames())
                    .filter(name -> name.toLowerCase().contains("kafka"))
                    .sorted()
                    .forEach(name -> System.out.println("Bean Kafka: " + name));
        };
    }


}
