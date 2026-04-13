package com.derocode.payment_grpc_server.records;

import lombok.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class PaymentConfirmation {
    private Integer paymentId;
    private String paymentDate;
    private String paymentMethod;
    private Integer orderId;
    private String orderReference;
    private double amount;
    private String status;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;


    @Override
    public int hashCode() {
        return Objects.hash(paymentId, paymentDate, paymentMethod, orderId, orderReference, amount, status, customerFirstName, customerLastName, customerEmail);
    }

    @Override
    public String toString() {
        return "PaymentConfirmation[" +
                "paymentId=" + paymentId + ", " +
                "paymentDate=" + paymentDate + ", " +
                "paymentMethod=" + paymentMethod + ", " +
                "orderId=" + orderId + ", " +
                "orderReference=" + orderReference + ", " +
                "amount=" + amount + ", " +
                "status=" + status + ", " +
                "customerFirstName=" + customerFirstName + ", " +
                "customerLastName=" + customerLastName + ", " +
                "customerEmail=" + customerEmail + ']';
    }

}
