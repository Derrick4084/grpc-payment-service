package com.derocode.payment_grpc_server.records;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class PaymentConfirmation {
    private Long paymentId;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private Long orderId;
    private String orderReference;
    private BigDecimal amount;
    private String status;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;
    private String eventId;


    @Override
    public int hashCode() {
        return Objects.hash(paymentId, paymentDate, paymentMethod, orderId, orderReference, amount, status, customerFirstName, customerLastName, customerEmail, eventId);
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
                "customerEmail=" + customerEmail + ", " +
                "eventId=" + eventId + ']';
    }

}
