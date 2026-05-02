package com.derocode.payment_grpc_server.clients;


import com.derocode.order.OrderServiceGrpc;
import com.derocode.order.OrderResponse;
import com.derocode.order.OrderRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class OrderGrpcClient {

    private final OrderServiceGrpc.OrderServiceBlockingStub stub;

    public OrderGrpcClient(@Value("${grpc.client.order.address}") String host) {

//        GrpcTracing grpcTracing = GrpcTracing.create(
//                Tracing.newBuilder()
//                        .localIp("127.0.0.1")
//                        .localPort(9144)
//                        .localServiceName("OrderService")
//                        .build()
//        );

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forTarget(host)
                .defaultServiceConfig(Map.of(
                        "methodConfig", List.of(Map.of(
                                "name", List.of(Map.of("service","order.OrderService")),
                                "retryPolicy", Map.of(
                                        "maxAttempts", 5.0,
                                        "initialBackoff", "0.5s",
                                        "maxBackoff", "5s",
                                        "backoffMultiplier", 2.0,
                                        "retryableStatusCodes", List.of("UNAVAILABLE", "DEADLINE_EXCEEDED")
                                )
                        ))
                ))
                .enableRetry()
                .usePlaintext()
                .build();
        this.stub = OrderServiceGrpc.newBlockingStub(managedChannel);
    }

    public OrderResponse getOrder(OrderRequest request){
        OrderResponse response = null;
        try {
            response = stub.getOrder(request);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                System.out.println(e.getStatus().getDescription());
            }
            return null;
        }
        return response;
    }
}
