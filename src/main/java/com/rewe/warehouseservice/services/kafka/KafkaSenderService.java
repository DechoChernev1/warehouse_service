package com.rewe.warehouseservice.services.kafka;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.UUID;

@Component
public class KafkaSenderService {
    @Value("${service.topic.name}")
    private String topicName;

    private final KafkaTemplate<Integer, String> template;

    public KafkaSenderService(KafkaTemplate<Integer, String> template) {
        this.template = template;
    }

    public void send(String toSend) {
        CloudEvent cloudEvent = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create("https://example.com/source"))
                .withType("com.example.MyEvent")
                .withDataContentType("application/json")
                .withData(toSend.getBytes())
                .build();

        template.send(topicName, cloudEvent.toString());
    }
}
