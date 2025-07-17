package br.com.btgpactual.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    public static final String ORDER_CREATED_TOPIC = "btg-pactual-order.created";

//    @Bean
//    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
//        return new Jackson2JsonMessageConverter();
//    }


    @Bean
    public NewTopic orderCreatedTopic() {
        return TopicBuilder.name(ORDER_CREATED_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
