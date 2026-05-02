package com.derocode.payment_grpc_server.clients;


import brave.Tracing;
import brave.grpc.GrpcTracing;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.derocode.customer.CustomerServiceGrpc;
import com.derocode.customer.CustomerResponse;
import com.derocode.customer.CustomerRequestById;


import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class CustomerGrpcClient {

    private final CustomerServiceGrpc.CustomerServiceBlockingStub stub;

    public CustomerGrpcClient(@Value("${grpc.client.customer.address}") String host) {

//                GrpcTracing grpcTracing = GrpcTracing.create(
//                Tracing.newBuilder()
//                        .localIp("127.0.0.1")
//                        .localPort(9144)
//                        .localServiceName("CustomerService")
//                        .build()
//                );

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forTarget(host)
                .defaultServiceConfig(Map.of(
                        "methodConfig", List.of(Map.of(
                                "name", List.of(Map.of("service","customer.CustomerService")),
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

        this.stub = CustomerServiceGrpc.newBlockingStub(managedChannel);
    }

    public CustomerResponse getCustomerById(CustomerRequestById request) {
        CustomerResponse response = null;
        try {
            response = stub.getCustomerById(request);
        } catch (StatusRuntimeException e) {
            if (e.getStatus().getCode() == Status.Code.NOT_FOUND) {
                System.out.println(e.getStatus().getDescription());
            }
            return null;
        }
        return response;
    }

}
