package com.derocode.payment_grpc_server.repository;

import com.derocode.payment_grpc_server.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
