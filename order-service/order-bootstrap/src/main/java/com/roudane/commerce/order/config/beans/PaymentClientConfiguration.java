package com.roudane.commerce.order.config.beans;


import com.roudane.commerce.common.web.client.TraceIdPropagationInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;


@Configuration
public class PaymentClientConfiguration {

    @Bean
    public RestClient paymentServiceRestClient(@Value("${payment-service.base-url}") String baseUrl) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestInterceptor(new TraceIdPropagationInterceptor())
                .build();
    }
}