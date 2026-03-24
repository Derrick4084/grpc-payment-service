package com.derocode.payment_grpc_server.kafka;

import com.derocode.payment_grpc_server.records.PaymentConfirmation;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class KafkaProducer {

    private final KafkaTemplate<String, PaymentConfirmation> kafkaTemplate;

    public KafkaProducer(KafkaTemplate<String, PaymentConfirmation> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(PaymentConfirmation message)
    {
        String paymentStatus = message.getStatus();

        if (Objects.equals(paymentStatus,"ACCEPTED")){
            kafkaTemplate.send("payment-success", message);
        } else if (Objects.equals(paymentStatus, "DENIED")) {
            kafkaTemplate.send("payment-failure", message);
        }

    }

}
