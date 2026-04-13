package com.derocode.payment_grpc_server.service;

import com.derocode.payment.GetPaymentRequest;
import com.derocode.payment.PaymentResponse;
import com.derocode.payment.PaymentRequest;
import com.derocode.payment.PaymentServiceGrpc;
import com.derocode.payment_grpc_server.configs.ServerInterceptorConfig;
import com.derocode.payment_grpc_server.kafka.KafkaProducer;


//import com.derocode.payment_grpc_server.mapper.LombokMapperImpl;
import com.derocode.payment_grpc_server.mapper.LombokMapperImpl;
import com.derocode.payment_grpc_server.models.Payment;
import com.derocode.payment_grpc_server.models.PaymentStatus;
import com.derocode.payment_grpc_server.records.PaymentConfirmation;
import com.derocode.payment_grpc_server.repository.PaymentRepository;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.service.GrpcService;

import java.util.Optional;

@GrpcService(interceptors = ServerInterceptorConfig.class)
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl extends PaymentServiceGrpc.PaymentServiceImplBase {

    private final PaymentRepository paymentRepository;
    private final KafkaProducer kafkaProducerService;
    private final LombokMapperImpl lombokMapper;

    private Integer longToInt(Long value) {
        if (value == null) return null;
        if (value > Integer.MAX_VALUE || value < Integer.MIN_VALUE) {
            throw new IllegalArgumentException("Value out of int range: " + value);
        }
        return value.intValue();
    }

    @Override
    public void createPayment(PaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {

        try{
            Payment entity = lombokMapper.reqToEntity(request);
            entity.setStatus(PaymentStatus.ACCEPTED);

            Payment savedEntity = null;
            try{
                savedEntity = paymentRepository.save(entity);
            } catch (RuntimeException e) {
                PaymentConfirmation paymentConfirmation = lombokMapper.errorPaymentConfirmation(request);

                log.info("Kafka payment failure with body: <{}>", paymentConfirmation);
                kafkaProducerService.sendMessage(paymentConfirmation);

                log.error("Unhandled exception in savePayment", e);
                responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
                return;
            }

            PaymentConfirmation paymentConfirmation = lombokMapper.entityToPaymentConfirmation(savedEntity);
            paymentConfirmation.setCustomerFirstName(request.getCustomerFirstName());
            paymentConfirmation.setCustomerLastName(request.getCustomerLastName());
            paymentConfirmation.setCustomerEmail(request.getCustomerEmail());
            paymentConfirmation.setStatus(PaymentStatus.ACCEPTED.name());
            log.info("Sending Kafka payment success with body: <{}>", paymentConfirmation);
            kafkaProducerService.sendMessage(paymentConfirmation);

            PaymentResponse paymentResponse = lombokMapper.entityToPaymentResponse(savedEntity);
            responseObserver.onNext(paymentResponse);
            responseObserver.onCompleted();

        } catch (Exception e) {
            log.error("Unhandled exception in createPayment", e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription(e.getMessage())
                            .withCause(e)
                            .asRuntimeException()
            );
        }
   }

    @Override
    public void getPayment(GetPaymentRequest request, StreamObserver<PaymentResponse> responseObserver) {
        Optional<Payment> payment = paymentRepository.findById(longToInt(request.getId()));
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