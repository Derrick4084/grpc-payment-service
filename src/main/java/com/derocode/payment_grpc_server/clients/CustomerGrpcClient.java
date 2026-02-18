package com.derocode.payment_grpc_server.clients;


import brave.Tracing;
import brave.grpc.GrpcTracing;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import com.derocode.customer.CustomerServiceGrpc;
import com.derocode.customer.CustomerResponse;

@Component
@Slf4j
public class CustomerGrpcClient {

    private final CustomerServiceGrpc.CustomerServiceBlockingStub stub;

    public CustomerGrpcClient(@Value("${spring.grpc.client.customer.port}") int port,
                              @Value("${spring.grpc.client.customer.host}") String host) {

        //        GrpcTracing grpcTracing = GrpcTracing.create(
//                Tracing.newBuilder()
//                        .localIp("127.0.0.1")
//                        .localPort(9144)
//                        .localServiceName("ProductService")
//                        .build()
//        );

        ManagedChannel managedChannel = ManagedChannelBuilder
                .forAddress(host,port)
                .usePlaintext()
                .build();

        this.stub = CustomerServiceGrpc.newBlockingStub(managedChannel);
    }

    public CustomerResponse getCustomerById(com.derocode.customer.CustomerRequest request) {
        return stub.getCustomerById(request);
    }

}
