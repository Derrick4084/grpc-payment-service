package com.derocode.payment_grpc_server.kafka;

import com.derocode.payment_grpc_server.exceptions.NonRetryableKafkaException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.ExponentialBackOffWithMaxRetries;

@Configuration
public class KafkaDefaultHandler {
    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, Object> kafkaTemplate) {
        // Sends failed messages to: <original-topic>.DLT
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(kafkaTemplate);
        // Retry 3 times with 2s delay between attempts
        ExponentialBackOffWithMaxRetries backOff = new ExponentialBackOffWithMaxRetries(3);
        backOff.setInitialInterval(1000L);
        backOff.setMultiplier(2.0);
        backOff.setMaxInterval(10000L);
        // Instantiate error handler
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
        // Mark which exceptions should NOT be retried, everything else will be retried
        errorHandler.addNotRetryableExceptions(
                NonRetryableKafkaException.class,
                IllegalArgumentException.class
        );
        return errorHandler;
    }
}
