package com.derocode.payment_grpc_server.kafka;

import com.derocode.payment_grpc_server.clients.CustomerGrpcClient;
import com.derocode.payment_grpc_server.clients.OrderGrpcClient;


import com.derocode.payment_grpc_server.mapper.LombokMapperImpl;
import com.derocode.payment_grpc_server.models.Payment;
import com.derocode.payment_grpc_server.models.PaymentStatus;
import com.derocode.payment_grpc_server.records.OrderConfirmation;
import com.derocode.order.OrderResponse;
import com.derocode.order.OrderRequest;
import com.derocode.customer.CustomerResponse;
import com.derocode.customer.CustomerRequest;
import com.derocode.payment_grpc_server.records.PaymentConfirmation;
import com.derocode.payment_grpc_server.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {


    private final OrderGrpcClient orderGrpcClient;
    private final CustomerGrpcClient customerGrpcClient;
    private final LombokMapperImpl lombokMapper;
    private final PaymentRepository repository;
    private final KafkaProducer kafka;

    @KafkaListener(topics = "order-info", id = "payment-svc-order", containerFactory = "orderKafkaListenerContainerFactory")
    public void consumeOrderConfirmation(OrderConfirmation orderConfirmation) throws MessagingException {

        String msg = String.format("Consuming message from order-info Topic:: %s", orderConfirmation);
        log.info(msg);

        String status = orderConfirmation.getStatus();

        if(Objects.equals(status,"PENDING_PAYMENT")) {
            OrderResponse orderResponse = orderGrpcClient.retrieveOrder(
                    OrderRequest.newBuilder()
                            .setId(orderConfirmation.getOrderId())
                            .build()
            );
            CustomerResponse customerResponse = customerGrpcClient.getCustomerById(
                    CustomerRequest.newBuilder()
                            .setId(orderResponse.getCustomerId())
                            .build()
            );

            Payment entity = lombokMapper.respToEntity(orderResponse);
            entity.setStatus(PaymentStatus.ACCEPTED);
            Payment savedPayment = repository.save(entity);

            PaymentConfirmation paymentConfirmation = lombokMapper.entityToPaymentConfirmation(savedPayment);
            paymentConfirmation.setCustomerFirstName(orderConfirmation.getCustomerFirstName());
            paymentConfirmation.setCustomerLastName(orderConfirmation.getCustomerLastName());
            paymentConfirmation.setCustomerEmail(orderConfirmation.getCustomerEmail());
            paymentConfirmation.setStatus(PaymentStatus.ACCEPTED.name());

            kafka.sendMessage(paymentConfirmation);

        }
    }
}
