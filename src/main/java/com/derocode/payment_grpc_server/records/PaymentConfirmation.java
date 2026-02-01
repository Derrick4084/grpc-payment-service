package com.derocode.payment_grpc_server.records;

import lombok.*;
import java.util.Objects;



@Getter
@Setter
@NoArgsConstructor
public class PaymentConfirmation {
    private String orderReference;
    private double amount;
    private String paymentDate;
    private String paymentMethod;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;

    @Override
    public int hashCode() {
        return Objects.hash(orderReference, amount, paymentDate, paymentMethod, customerFirstName, customerLastName, customerEmail);
    }

    @Override
    public String toString() {
        return "PaymentConfirmation[" +
                "orderReference=" + orderReference + ", " +
                "amount=" + amount + ", " +
                "paymentDate=" + paymentDate + ", " +
                "paymentMethod=" + paymentMethod + ", " +
                "customerFirstName=" + customerFirstName + ", " +
                "customerLastName=" + customerLastName + ", " +
                "customerEmail=" + customerEmail + ']';
    }

}
