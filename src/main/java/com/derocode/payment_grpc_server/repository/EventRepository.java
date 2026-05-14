package com.derocode.payment_grpc_server.repository;

import com.derocode.payment_grpc_server.models.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, String> {
}
