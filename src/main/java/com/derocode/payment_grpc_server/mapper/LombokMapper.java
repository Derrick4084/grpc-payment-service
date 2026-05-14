package com.derocode.payment_grpc_server.mapper;

import com.derocode.payment.PaymentResponse;
import com.derocode.payment_grpc_server.models.Payment;
import com.derocode.payment_grpc_server.models.PaymentMethod;
import com.derocode.payment_grpc_server.models.PaymentStatus;
import com.derocode.payment_grpc_server.records.PaymentConfirmation;
import com.derocode.order.OrderResponse;
import org.jspecify.annotations.NonNull;
import org.mapstruct.*;
import com.derocode.payment.PaymentRequest;

import java.math.BigDecimal;


@Mapper(uses = DateMapper.class,
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED
)
public interface LombokMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "amount", source = "totalAmount", qualifiedByName = "doubleToBigDecimal")
    @Mapping(target = "paymentMethod", qualifiedByName = "paymentMethodObject")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "orderReference", source = "reference")
    @Mapping(target = "paymentDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastModifiedDate", ignore = true)
    Payment respToEntity(OrderResponse response);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "amount", qualifiedByName = "doubleToBigDecimal")
    @Mapping(target = "paymentMethod", qualifiedByName = "paymentMethodObject")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "paymentDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastModifiedDate", ignore = true)
    Payment reqToEntity(PaymentRequest request);


    @Mapping(target = "amount", source = "amount", qualifiedByName = "bigDecimalToDouble")
    @Mapping(target = "paymentMethod", source = "paymentMethod", qualifiedByName = "paymentMethodString")
    @Mapping(target = "status", source = "status", qualifiedByName = "paymentStatusString")
    PaymentResponse entityToPaymentResponse(Payment entity);


    @Mapping(target = "paymentId", source = "entity.id")
    @Mapping(target = "paymentDate", source = "entity.paymentDate")
    @Mapping(target = "paymentMethod", source = "entity.paymentMethod", qualifiedByName = "paymentMethodString")
    @Mapping(target = "orderId", source = "entity.orderId")
    @Mapping(target = "orderReference", source = "entity.orderReference")
    @Mapping(target = "amount", source = "entity.amount", qualifiedByName = "bigDecimalToDouble")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "customerFirstName", ignore = true)
    @Mapping(target = "customerLastName", ignore = true)
    @Mapping(target = "customerEmail", ignore = true)
    PaymentConfirmation entityToPaymentConfirmation(Payment entity);

    @Mapping(target = "status", expression = "java(com.derocode.payment_grpc_server.models.PaymentStatus.ERROR.name())")
    @Mapping(target = "paymentId", ignore = true)
    @Mapping(target = "paymentDate", ignore = true)
    PaymentConfirmation errorPaymentConfirmation(PaymentRequest paymentRequest);

    @Named("doubleToBigDecimal")
    default BigDecimal doubleToBigDecimal(double price) {
        return BigDecimal.valueOf(price);
    }

    @Named("bigDecimalToDouble")
    default double bigDecimalToDouble(@NonNull BigDecimal amount) {
        return amount.doubleValue();
    }

    @Named("paymentMethodObject")
    default PaymentMethod paymentMethodObject(String paymentMethod){
        return PaymentMethod.valueOf(paymentMethod);
    }

    @Named("paymentMethodString")
    default String paymentMethodString(@NonNull PaymentMethod paymentMethod){
        return paymentMethod.name();
    }

    @Named("paymentStatusString")
    default String paymentStatusString(@NonNull PaymentStatus paymentStatus){
        return paymentStatus.name();
    }


}
