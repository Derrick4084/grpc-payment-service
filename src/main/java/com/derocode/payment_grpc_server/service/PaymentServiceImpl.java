package com.derocode.payment_grpc_server.service;

import com.derocode.payment.PaymentResponse;
import com.derocode.payment.PaymentRequest;
import com.derocode.payment.PaymentServiceGrpc;
import com.derocode.payment_grpc_server.configs.ServerInterceptorConfig;
import com.derocode.payment_grpc_server.kafka.KafkaProducer;
import com.derocode.payment_grpc_server.mapper.LombokMapperImpl;
import com.derocode.payment_grpc_server.models.Payment;
import com.derocode.payment_grpc_server.records.PaymentConfirmation;
import com.derocode.payment_grpc_server.repository.PaymentRepository;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.time.LocalDateTime;

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
        Payment savedEntity = paymentRepository.save(entity);

        PaymentResponse paymentResponse = lombokMapper.toResponse(savedEntity);

        PaymentConfirmation paymentConfirmation = lombokMapper.respToConfirmation(paymentResponse);
        paymentConfirmation.setCustomerEmail(request.getCustomerEmail());
        paymentConfirmation.setCustomerFirstName(request.getCustomerFirstName());
        paymentConfirmation.setCustomerLastName(request.getCustomerLastName());

        log.info("Kafka notification request with body: <{}>", paymentConfirmation);

        kafkaProducerService.sendMessage(paymentConfirmation);

        responseObserver.onNext(paymentResponse);
        responseObserver.onCompleted();

    }
}