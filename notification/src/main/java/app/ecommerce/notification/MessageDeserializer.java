package app.ecommerce.notification;

import app.ecommerce.notification.dto.OrderMessage;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public final class MessageDeserializer implements org.apache.kafka.common.serialization.Deserializer<OrderMessage>{

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public OrderMessage deserialize(String s, byte[] bytes) {
        if(bytes == null) return null;

        try {
            return objectMapper.readValue(bytes, OrderMessage.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing message", e);
        }
    }
}
