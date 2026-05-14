package com.derocode.payment_grpc_server.exceptions;

public class NonRetryableKafkaException extends RuntimeException{
    public NonRetryableKafkaException(String message) {
        super(message);
    }
}
