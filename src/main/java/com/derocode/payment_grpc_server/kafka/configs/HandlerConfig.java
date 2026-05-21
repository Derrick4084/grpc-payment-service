package com.derocode.payment_grpc_server.kafka.configs;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class HandlerConfig {

    private final ProducerBaseProps producerBaseProps;

    public HandlerConfig(ProducerBaseProps producerBaseProps) {
        this.producerBaseProps = producerBaseProps;
    }

    @Bean
    public ProducerFactory<String, Object> handlerProducerFactory() {
        Map<String, Object> config = new HashMap<>(producerBaseProps.producerProps());
        config.put(JacksonJsonSerializer.TYPE_MAPPINGS, "orderConfirmation:com.derocode.payment_grpc_server.records.OrderConfirmation," +
                "paymentConfirmation:com.derocode.payment_grpc_server.records.PaymentConfirmation,");
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> handlerKafkaTemplate() {
        return new KafkaTemplate<>(handlerProducerFactory());
    }
}
