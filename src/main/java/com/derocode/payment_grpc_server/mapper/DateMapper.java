package com.derocode.payment_grpc_server.mapper;

import com.google.protobuf.Timestamp;

import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public class DateMapper {

    public Timestamp map(LocalDateTime value) {
        Instant instant = value.toInstant(ZoneOffset.UTC);
        return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    public LocalDateTime map(Timestamp value) {
        Instant instant = Instant.ofEpochSecond(value.getSeconds(), value.getNanos());
        ZoneId zoneId = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zoneId);
    }

}
