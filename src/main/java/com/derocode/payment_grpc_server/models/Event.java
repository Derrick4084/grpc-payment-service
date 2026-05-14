package com.derocode.payment_grpc_server.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
@Table(name = "processed_events")
public class Event implements Serializable {
    @Id
    private String eventId;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime processedAt;
}
