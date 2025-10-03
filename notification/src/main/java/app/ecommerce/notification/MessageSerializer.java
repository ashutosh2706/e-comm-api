package app.ecommerce.notification;

import app.ecommerce.notification.dto.OrderMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

import java.io.IOException;

public final class MessageSerializer implements Serializer<OrderMessage> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String s, OrderMessage orderMessage) {
        if(orderMessage == null) return null;

        try {
            return objectMapper.writeValueAsBytes(orderMessage);
        } catch (IOException e) {
            throw new RuntimeException("Error in serializing OrderMessage for kafka topic " + e.getMessage());
        }
    }
}
