package br.com.btgpactual.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {
    public static final String ORDER_CREATED_TOPIC = "btg-pactual-order.created";

    @Value("${spring.kafka.topic.partitions:1}")
    private int partitions;

    @Value("${spring.kafka.retry.interval-ms:1000}")
    private long retryInterval;

    @Value("${spring.kafka.retry.max-attempts:3}")
    private int maxAttempts;

    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(ORDER_CREATED_TOPIC)
                .partitions(partitions)
                .replicas(1)
                .build();
    }

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<Object, Object> template) {
        // 1. Configura o recuperador que envia para a DLQ
        // Forçamos o nome do tópico para usar o sufixo .DLT e manter a mesma partição
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template,
                (r, e) -> new TopicPartition(r.topic() + ".DLT", r.partition()));

        // 2. Configura as tentativas (Retries) antes de desistir
        // Usa os valores injetados do application.properties
        FixedBackOff backOff = new FixedBackOff(retryInterval, maxAttempts);

        return new DefaultErrorHandler(recoverer, backOff);
    }

    @Bean
    public NewTopic orderCreatedDLT() {
        // O nome deve ser: "btg-pactual-order.created.DLT"
        return TopicBuilder.name(ORDER_CREATED_TOPIC + ".DLT")
                .partitions(partitions) // Importante: Mesma qtd de partições do tópico principal
                .replicas(1)
                .build();
    }
}
