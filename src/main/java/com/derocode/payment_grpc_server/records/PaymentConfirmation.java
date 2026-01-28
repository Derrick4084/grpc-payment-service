package com.derocode.payment_grpc_server.records;

import com.derocode.payment_grpc_server.models.PaymentMethod;


public record PaymentConfirmation(
        String orderReference,
        double amount,
        PaymentMethod paymentMethod,
        String customerFirstName,
        String customerLastName,
        String customerEmail
) {
}
