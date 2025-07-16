package com.rewe.warehouseservice.services.kafka;

import com.rewe.warehouseservice.data.entities.ProducedMessages;
import com.rewe.warehouseservice.data.repositories.ProducedMessagesRepository;
import com.rewe.warehouseservice.enums.ProducedMessageType;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.io.IOException;
import java.net.URI;
import java.util.UUID;

@Component
@Slf4j
public class KafkaSenderService {
    private final KafkaTemplate<Integer, String> template;
    private final ProducedMessagesRepository messagesRepository;
    private String topicName;

    public KafkaSenderService(
            KafkaTemplate<Integer, String> template,
            ProducedMessagesRepository messagesRepository,
            @Value("${service.topic.name}")
            String topicName) {
        this.template = template;
        this.messagesRepository = messagesRepository;
        this.topicName = topicName;
    }

    public void send(ProducedMessages producedMessages, String cloudEventType) throws IOException {
        log.info("Before cloud event Sending to {} - topic: {}", producedMessages, topicName);
        byte[] data = SerializationUtils.serialize(producedMessages);

        CloudEvent cloudEvent = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create("/warehouses"))
                .withType(cloudEventType)
                .withDataContentType("application/json")
                .withData(data)
                .build();

        var future = template.send(topicName, cloudEvent.toString());
        future.whenComplete((sendResults, exception) -> {
            if (exception != null) {
                producedMessages.setStatus(ProducedMessageType.FAILED.toString());
                messagesRepository.save(producedMessages);
            } else {
                producedMessages.setStatus(ProducedMessageType.SENT.toString());
                messagesRepository.save(producedMessages);
            }
        });
        log.info("After cloud event Sending to {} - topic: {}", producedMessages, topicName);
    }
}
