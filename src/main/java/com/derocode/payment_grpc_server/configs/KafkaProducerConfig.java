package com.derocode.payment_grpc_server.configs;


import com.derocode.payment_grpc_server.records.PaymentConfirmation;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootStrapServers;

    private Map<String, Object> baseConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootStrapServers);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,"SASL_SSL");
        props.put(SaslConfigs.SASL_MECHANISM,"AWS_MSK_IAM");
        props.put(SaslConfigs.SASL_JAAS_CONFIG,"software.amazon.msk.auth.iam.IAMLoginModule required;");
        props.put(SaslConfigs.SASL_CLIENT_CALLBACK_HANDLER_CLASS,"software.amazon.msk.auth.iam.IAMClientCallbackHandler");
        return props;
    }


    @Bean
    public ProducerFactory<String, PaymentConfirmation> producerFactory() {
        Map<String, Object> config = new HashMap<>(baseConfig());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);
        config.put(JacksonJsonSerializer.TYPE_MAPPINGS, "paymentConfirmation:com.derocode.payment_grpc_server.records.PaymentConfirmation");
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RETRIES_CONFIG, 10);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, 5);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, PaymentConfirmation> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


    @Bean
    public KafkaAdmin admin() {
        Map<String,Object> config = new HashMap<>(baseConfig());
        return new KafkaAdmin(config);
    }

    @Bean
    public NewTopic paymentTopic() {
        return TopicBuilder.name("payment")
                .partitions(3)
                .replicas(2)
                .build();
    }


}
