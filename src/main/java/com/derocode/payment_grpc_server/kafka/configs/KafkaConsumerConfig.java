package com.derocode.payment_grpc_server.kafka.configs;

import com.derocode.payment_grpc_server.records.OrderConfirmation;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.Map;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final ConsumerBaseProps consumerBaseProps;

    @Bean
    public ConsumerFactory<String, OrderConfirmation> orderConsumerFactory() {
        Map<String, Object> props = consumerBaseProps.baseConfig();
        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                new JacksonJsonDeserializer<>(OrderConfirmation.class, false)
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,OrderConfirmation> orderKafkaListenerContainerFactory(DefaultErrorHandler handler) {
        ConcurrentKafkaListenerContainerFactory<String,OrderConfirmation> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(orderConsumerFactory());
        factory.setCommonErrorHandler(handler);
        factory.setConcurrency(3);
        factory.getContainerProperties().setPollTimeout(3000);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
        return factory;
    }
}
