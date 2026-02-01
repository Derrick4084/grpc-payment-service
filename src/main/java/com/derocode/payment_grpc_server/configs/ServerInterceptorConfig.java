package com.derocode.payment_grpc_server.configs;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ServerInterceptorConfig implements ServerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(ServerInterceptorConfig.class);

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {

        log.info("Intercepting client call for method: {}", serverCall.getMethodDescriptor().getFullMethodName());
        log.info("Headers {}:", metadata);

        return serverCallHandler.startCall(serverCall, metadata);
    }
}
