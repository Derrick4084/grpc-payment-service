package com.derocode.payment_grpc_server.service;


import com.derocode.order.OrderResponse;
import com.derocode.order.OrderRequest;
import com.derocode.payment.GetPaymentRequest;
import com.derocode.payment.PaymentResponse;
import com.derocode.payment.PaymentRequest;
import com.derocode.payment.PaymentServiceGrpc;
import com.derocode.payment_grpc_server.clients.OrderGrpcClient;
import com.derocode.payment_grpc_server.configs.ServerInterceptorConfig;
import com.derocode.payment_grpc_server.kafka.producers.PaymentProducer;
import com.derocode.payment_grpc_server.mapper.LombokMapperImpl;
import com.derocode.payment_grpc_server.models.Payment;
import com.derocode.payment_grpc_server.models.PaymentStatus;
import com.derocode.payment_grpc_server.records.PaymentConfirmation;
import com.derocode.payment_grpc_server.repository.PaymentRepository;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.messaging.MessagingException;

import java.util.Optional;
import java.util.UUID;

@GrpcService(interceptors = ServerInterceptorConfig.class)
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl extends PaymentServiceGrpc.PaymentServiceImplBase {

    private final PaymentRepository paymentRepository;
    private final PaymentProducer kafka;
    private final LombokMapperImpl lombokMapper;
    private final OrderGrpcClient orderGrpcClient;

    private @NonNull String generateEventId() {
        return UUID.randomUUID().toString().toUpperCase();
    }

    @Override
    public void createPayment(PaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {

        try {
            OrderResponse orderResponse =
                    orderGrpcClient.getOrder(OrderRequest.newBuilder()
                            .setId(request.getOrderId())
                            .build());

            Payment entity = lombokMapper.reqToEntity(request);
            entity.setStatus(PaymentStatus.ACCEPTED);

            Payment savedEntity = paymentRepository.save(entity);

            // Create the payment confirmation for kafka
            PaymentConfirmation paymentConfirmation = lombokMapper.entityToPaymentConfirmation(savedEntity);
            paymentConfirmation.setCustomerFirstName(request.getCustomerFirstName());
            paymentConfirmation.setCustomerLastName(request.getCustomerLastName());
            paymentConfirmation.setCustomerEmail(request.getCustomerEmail());

            paymentConfirmation.setStatus(PaymentStatus.ACCEPTED.name());
            paymentConfirmation.setEventId(generateEventId());
            PaymentResponse paymentResponse = lombokMapper.entityToPaymentResponse(savedEntity);

            // Send payment to kafka
            log.info("Sending Kafka payment success with body: <{}>", paymentConfirmation);
            kafka.sendMessage(paymentConfirmation);

            // Send grpc response
            responseObserver.onNext(paymentResponse);
            responseObserver.onCompleted();

        } catch (StatusRuntimeException sre) {
            switch (sre.getStatus().getCode()) {
                case NOT_FOUND -> responseObserver.onError(
                        Status.NOT_FOUND
                                .withDescription(sre.getStatus().getDescription())
                                .asRuntimeException());
                case ALREADY_EXISTS -> responseObserver.onError(
                        Status.ALREADY_EXISTS
                                .withDescription(sre.getStatus().getDescription())
                                .asRuntimeException()
                );
                case INVALID_ARGUMENT -> responseObserver.onError(
                        Status.INVALID_ARGUMENT
                                .withDescription(sre.getStatus().getDescription())
                                .asRuntimeException()
                );
                default -> responseObserver.onError(
                        sre.getStatus()
                                .withCause(sre)
                                .asRuntimeException()
                );
            }
        } catch (IllegalArgumentException | OptimisticLockingFailureException e) {
            responseObserver.onError(
                    Status.ALREADY_EXISTS
                            .withDescription("Payment already exists for this order")
                            .asRuntimeException()
            );
        } catch (Exception e) {
            log.error("Unexpected error while creating payment", e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("Internal server error")
                            .withCause(e)
                            .asRuntimeException()
            );
        }
   }

    @Override
    public void getPayment(GetPaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {
        Optional<Payment> payment = paymentRepository.findById(request.getId());
        if(payment.isEmpty())
        {
            log.error("No payment found with id: {}", request.getId());
            responseObserver.onError(
                    Status.NOT_FOUND
                            .withDescription("No payment found with id: " + request.getId())
                            .asRuntimeException()
            );
        } else {
            PaymentResponse paymentResponse = lombokMapper.entityToPaymentResponse(payment.get());
            responseObserver.onNext(paymentResponse);
            responseObserver.onCompleted();

        }
    }
}