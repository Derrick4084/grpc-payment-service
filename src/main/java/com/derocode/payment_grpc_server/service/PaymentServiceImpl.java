package com.derocode.payment_grpc_server.service;

import com.derocode.payment.PaymentResponse;
import com.derocode.payment.PaymentRequest;
import com.derocode.payment.PaymentServiceGrpc;
import com.derocode.payment_grpc_server.configs.ServerInterceptorConfig;
import com.derocode.payment_grpc_server.kafka.KafkaProducer;
import com.derocode.payment_grpc_server.mapper.LombokMapperImpl;
import com.derocode.payment_grpc_server.models.Payment;
import com.derocode.payment_grpc_server.models.PaymentStatus;
import com.derocode.payment_grpc_server.records.PaymentConfirmation;
import com.derocode.payment_grpc_server.repository.PaymentRepository;
import com.google.protobuf.Timestamp;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.grpc.server.service.GrpcService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;

@GrpcService(interceptors = ServerInterceptorConfig.class)
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl extends PaymentServiceGrpc.PaymentServiceImplBase {

    private final PaymentRepository paymentRepository;
    private final KafkaProducer kafkaProducerService;
    private final LombokMapperImpl lombokMapper;

    @Override
    public void createPayment(PaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {

        Payment entity = lombokMapper.toEntity(request);
        entity.setPaymentDate(LocalDateTime.now());
        entity.setStatus(PaymentStatus.ACCEPTED);

        Payment savedEntity = null;
        try{
            savedEntity = paymentRepository.save(entity);
        } catch (DataAccessException e) {

            PaymentConfirmation paymentConfirmation = lombokMapper.reqToConfirmation(request);
            paymentConfirmation.setPaymentDate(LocalDateTime.now().toString());
            paymentConfirmation.setStatus(PaymentStatus.DENIED.name());
            log.info("Kafka payment failure with body: <{}>", paymentConfirmation);
            kafkaProducerService.sendMessage(paymentConfirmation);
            PaymentResponse paymentResponse = lombokMapper.entityToResponse(entity);
            responseObserver.onNext(paymentResponse);
            responseObserver.onCompleted();
        }

        if (Objects.nonNull(savedEntity)) {
            PaymentConfirmation paymentConfirmation = lombokMapper.entityToConfirmation(savedEntity);
            paymentConfirmation.setCustomerFirstName(request.getCustomerFirstName());
            paymentConfirmation.setCustomerLastName(request.getCustomerLastName());
            paymentConfirmation.setCustomerEmail(request.getCustomerEmail());
            paymentConfirmation.setStatus(PaymentStatus.ACCEPTED.name());

            log.info("Sending Kafka payment success with body: <{}>", paymentConfirmation);
            kafkaProducerService.sendMessage(paymentConfirmation);

            PaymentResponse paymentResponse = lombokMapper.entityToResponse(savedEntity);
            responseObserver.onNext(paymentResponse);
            responseObserver.onCompleted();

        }






    }
}