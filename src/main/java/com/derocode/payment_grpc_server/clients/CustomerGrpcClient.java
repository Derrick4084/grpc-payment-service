package com.derocode.payment_grpc_server.clients;


import brave.Tracing;
import brave.grpc.GrpcTracing;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.derocode.customer.CustomerServiceGrpc;
import com.derocode.customer.CustomerResponse;

@Service
@Slf4j
public class CustomerGrpcClient {


    private final ManagedChannel managedChannel;
    private final CustomerServiceGrpc.CustomerServiceBlockingStub customerServiceBlockingStub;


    public CustomerGrpcClient() {
        this(ManagedChannelBuilder.forAddress("localhost", 8050).usePlaintext());
    }

    public CustomerGrpcClient(ManagedChannelBuilder<?> usePlainText) {

        GrpcTracing grpcTracing = GrpcTracing.create(
                Tracing.newBuilder()
                        .localIp("127.0.0.1")
                        .localPort(9144)
                        .localServiceName("CustomerService")
                        .build()
        );
        managedChannel = usePlainText.intercept(grpcTracing.newClientInterceptor()).build();
        customerServiceBlockingStub = CustomerServiceGrpc.newBlockingStub(managedChannel);
    }

    public CustomerResponse getCustomerById(com.derocode.customer.CustomerRequest request){
        return customerServiceBlockingStub.getCustomerById(request);
    }

    public void disconnectChannel() {
        final ManagedChannel shutdown = managedChannel.shutdown();
    }


}
