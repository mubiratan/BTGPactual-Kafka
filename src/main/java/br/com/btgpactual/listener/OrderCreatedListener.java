package br.com.btgpactual.listener;

import br.com.btgpactual.listener.dto.OrderCreatedEvent;
import br.com.btgpactual.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import static br.com.btgpactual.config.KafkaConfig.ORDER_CREATED_TOPIC;

@Component
public class OrderCreatedListener {

    private final Logger logger = LoggerFactory.getLogger(OrderCreatedListener.class);

    private final OrderService orderService;

    public OrderCreatedListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = ORDER_CREATED_TOPIC)
    public void listen(Message<OrderCreatedEvent> message, @Header(KafkaHeaders.RECEIVED_PARTITION) int partition) {
        logger.info("Message consumed from partition {}: {}", partition, message.getPayload());
        orderService.save(message.getPayload());
    }
}
