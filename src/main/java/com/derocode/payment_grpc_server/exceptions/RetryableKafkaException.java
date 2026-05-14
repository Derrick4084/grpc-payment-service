package com.derocode.payment_grpc_server.exceptions;

public class RetryableKafkaException extends RuntimeException{
    public RetryableKafkaException(String message, Throwable cause) {
        super(message, cause);
    }
}
