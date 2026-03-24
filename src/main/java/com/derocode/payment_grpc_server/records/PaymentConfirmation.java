package com.derocode.payment_grpc_server.records;

import lombok.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class PaymentConfirmation {
    private Integer orderId;
    private String orderReference;
    private double amount;
    private String paymentDate;
    private String paymentMethod;
    private String status;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;


    @Override
    public int hashCode() {
        return Objects.hash(orderId, orderReference, amount, paymentDate, paymentMethod, status, customerFirstName, customerLastName, customerEmail);
    }

    @Override
    public String toString() {
        return "PaymentConfirmation[" +
                "orderId=" + orderId + ", " +
                "orderReference=" + orderReference + ", " +
                "amount=" + amount + ", " +
                "paymentDate=" + paymentDate + ", " +
                "paymentMethod=" + paymentMethod + ", " +
                "status=" + status + ", " +
                "customerFirstName=" + customerFirstName + ", " +
                "customerLastName=" + customerLastName + ", " +
                "customerEmail=" + customerEmail + ']';
    }

}
