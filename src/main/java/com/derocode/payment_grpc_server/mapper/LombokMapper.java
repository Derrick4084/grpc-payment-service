package com.derocode.payment_grpc_server.mapper;

import com.derocode.payment.PaymentResponse;
import com.derocode.payment_grpc_server.models.Payment;
import com.derocode.payment_grpc_server.models.PaymentMethod;
import com.derocode.payment_grpc_server.records.PaymentConfirmation;
import org.mapstruct.Mapper;
import com.derocode.payment.PaymentRequest;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.math.BigDecimal;


@Mapper(uses = DateMapper.class ,componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LombokMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentMethod", qualifiedByName = "paymentMethodObject")
    @Mapping(target = "amount", qualifiedByName = "doubleToBigDecimal")
    @Mapping(target = "paymentDate", ignore = true)
    @Mapping(target = "lastModifiedDate", ignore = true)
    Payment toEntity(PaymentRequest request);


    @Mapping(target = "paymentMethod", qualifiedByName = "paymentMethodString")
    @Mapping(target = "amount", qualifiedByName = "bigDecimalToDouble")
    @Mapping(target = "paymentDate", source = "paymentDate")
    PaymentResponse entityToResponse(Payment entity);

    @Mapping(target = "paymentMethod", qualifiedByName = "paymentMethodString")
    @Mapping(target = "amount", qualifiedByName = "bigDecimalToDouble")
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "customerFirstName", ignore = true)
    @Mapping(target = "customerLastName", ignore = true)
    @Mapping(target = "customerEmail", ignore = true)
    PaymentConfirmation entityToConfirmation(Payment entity);

    @Mapping(target = "paymentDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    PaymentConfirmation reqToConfirmation(PaymentRequest paymentRequest);

    @Named("doubleToBigDecimal")
    default BigDecimal doubleToBigDecimal(double price) {
        return BigDecimal.valueOf(price);
    }

    @Named("bigDecimalToDouble")
    default double bigDecimalToDouble(BigDecimal amount) {
        return amount.doubleValue();
    }

    @Named("paymentMethodObject")
    default PaymentMethod paymentMethodObject(String paymentMethod){
        return PaymentMethod.valueOf(paymentMethod);
    }

    @Named("paymentMethodString")
    default String paymentMethodString(PaymentMethod paymentMethod){
        return paymentMethod.name();
    }

}
