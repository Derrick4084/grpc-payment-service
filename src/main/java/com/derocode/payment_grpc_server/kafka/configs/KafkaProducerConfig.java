package com.derocode.payment_grpc_server.kafka.configs;


import com.derocode.payment_grpc_server.records.PaymentConfirmation;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaProducerConfig {

    private final ProducerBaseProps producerBaseProps;

    @Bean
    public ProducerFactory<String, PaymentConfirmation> producerFactory() {
        Map<String, Object> config = new HashMap<>(producerBaseProps.baseConfig());
        config.put(JacksonJsonSerializer.TYPE_MAPPINGS, "paymentConfirmation:com.derocode.payment_grpc_server.records.PaymentConfirmation");
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, PaymentConfirmation> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


}
