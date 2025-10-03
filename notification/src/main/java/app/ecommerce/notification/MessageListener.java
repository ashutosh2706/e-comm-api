package app.ecommerce.notification;

import app.ecommerce.notification.dto.OrderMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
public class MessageListener {

    @Value("${app.config.kafka.topic}")
    private String kafkaTopic;

    @Value("${app.config.kafka.group_id}")
    private String groupId;
    Logger logger = LoggerFactory.getLogger(MessageListener.class);

    @KafkaListener(topics = "order-message", groupId = "order-message-consumer")
    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 1000, maxDelay = 5000, multiplier = 2),
            exclude = {NullPointerException.class}
    )
    public void messageConsumer1(OrderMessage orderMessage, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                 @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partitionId) {
        logger.info("Consumer1 Received: {} from {} partition {}", orderMessage.toString(), topic, partitionId);
    }

    /// consume message in dlt
    @DltHandler
    public void listenDLT(OrderMessage message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        logger.error("DLT received: {}, from {}", message.toString(), topic);
    }
}
