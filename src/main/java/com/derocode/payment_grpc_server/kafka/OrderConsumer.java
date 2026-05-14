package com.derocode.payment_grpc_server.kafka;

import com.derocode.payment_grpc_server.clients.OrderGrpcClient;


import com.derocode.payment_grpc_server.exceptions.RetryableKafkaException;
import com.derocode.payment_grpc_server.mapper.LombokMapperImpl;
import com.derocode.payment_grpc_server.models.Event;
import com.derocode.payment_grpc_server.models.Payment;
import com.derocode.payment_grpc_server.models.PaymentStatus;
import com.derocode.payment_grpc_server.records.OrderConfirmation;
import com.derocode.order.OrderResponse;
import com.derocode.order.OrderRequest;
import com.derocode.payment_grpc_server.records.PaymentConfirmation;
import com.derocode.payment_grpc_server.repository.EventRepository;
import com.derocode.payment_grpc_server.repository.PaymentRepository;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderConsumer {

    private final OrderGrpcClient orderGrpcClient;
    private final LombokMapperImpl lombokMapper;
    private final PaymentRepository paymentRepository;
    private final PaymentProducer kafka;
    private final EventRepository eventRepository;

    @KafkaListener(topics = "order-info", id = "payment-svc-order", containerFactory = "orderKafkaListenerContainerFactory")
    public void consumeOrderConfirmation(OrderConfirmation orderConfirmation) throws MessagingException {

        String msg = String.format("Consuming message from order-info Topic:: %s", orderConfirmation);
        log.info(msg);


        String eventId = orderConfirmation.getEventId();
        try {
            eventRepository.save(Event.builder().eventId(eventId).build());
        } catch (DuplicateKeyException | IllegalArgumentException | OptimisticLockingFailureException e) {
            log.info("Duplicate event ignored: {}", eventId);
            return;
        }

        String status = orderConfirmation.getStatus();
        if (!"PENDING_PAYMENT".equals(orderConfirmation.getStatus())) {
            return;
        };

        OrderResponse orderResponse = null;
        try {
            orderResponse = orderGrpcClient.getOrder(
                    OrderRequest.newBuilder()
                            .setId(orderConfirmation.getOrderId())
                            .build()
            );
        } catch (StatusRuntimeException e) {
            throw new RetryableKafkaException(e.getMessage(), e.getCause());

        }
        Payment entity = lombokMapper.respToEntity(orderResponse);
        entity.setStatus(PaymentStatus.ACCEPTED);
        Payment savedPayment = null;
        try {
            savedPayment = paymentRepository.save(entity);
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            throw new RuntimeException("Problem saving payment");
        }

        PaymentConfirmation paymentConfirmation = lombokMapper.entityToPaymentConfirmation(savedPayment);
        paymentConfirmation.setCustomerFirstName(orderConfirmation.getCustomerFirstName());
        paymentConfirmation.setCustomerLastName(orderConfirmation.getCustomerLastName());
        paymentConfirmation.setCustomerEmail(orderConfirmation.getCustomerEmail());
        paymentConfirmation.setStatus(PaymentStatus.ACCEPTED.name());

        kafka.sendMessage(paymentConfirmation);
    }
}



