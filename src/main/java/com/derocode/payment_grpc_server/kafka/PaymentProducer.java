package com.derocode.payment_grpc_server.kafka;

import com.derocode.payment_grpc_server.records.PaymentConfirmation;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentProducer {

    private final KafkaTemplate<String, PaymentConfirmation> kafkaTemplate;

    public PaymentProducer(KafkaTemplate<String, PaymentConfirmation> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(PaymentConfirmation message)
    {
        kafkaTemplate.send("payment-info", message);
    }

}
