package com.derocode.payment_grpc_server.records;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class OrderConfirmation {
    private Integer orderId;
    private String reference;
    private String status;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private LocalDateTime orderDate;
    private String customerFirstName;
    private String customerLastName;
    private String customerEmail;

    @Override
    public int hashCode() {
        return Objects.hash(orderId, reference, status, totalAmount, paymentMethod, customerFirstName, customerLastName, customerEmail);
    }

    @Override
    public String toString() {
        return "OrderConfirmation[" +
                "orderId=" + orderId + ", " +
                "reference=" + reference + ", " +
                "status=" + status + ", " +
                "totalAmount=" + totalAmount + ", " +
                "paymentMethod=" + paymentMethod + ", " +
                "customerFirstName=" + customerFirstName + ", " +
                "customerLastName=" + customerLastName + ", " +
                "customerEmail=" + customerEmail + ']';
    }
}
