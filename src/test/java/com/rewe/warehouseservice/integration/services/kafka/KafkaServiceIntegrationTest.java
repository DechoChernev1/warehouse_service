package com.rewe.warehouseservice.integration.services.kafka;

import com.rewe.warehouseservice.WarehouseServiceApplication;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.apache.kafka.clients.consumer.ConsumerConfig.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Testcontainers
@SpringBootTest(classes = WarehouseServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class KafkaServiceIntegrationTest {
    @ServiceConnection
    @Container
    private static final KafkaContainer kafkaContainer = new KafkaContainer(DockerImageName.parse("apache/kafka-native:3.8.0"));

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${service.topic.name}")
    private String topicName;

    private Consumer<String, String> consumer;

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @BeforeEach
    void setUp() {
        Map<String, Object> consumerProps = Map.of(
                ENABLE_AUTO_COMMIT_CONFIG, "true",
                GROUP_ID_CONFIG, "test-group",
                KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                AUTO_OFFSET_RESET_CONFIG, "earliest",
                BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers()
        );

        consumer = new DefaultKafkaConsumerFactory<String, String>(consumerProps).createConsumer();
        consumer.subscribe(Collections.singletonList(topicName));
    }

    @Test
    void shouldHandleWarehouseEvent() {

        CloudEvent cloudEvent = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create("https://example.com/source"))
                .withType("com.example.MyEvent")
                .withDataContentType("application/json")
                .withData("Msg".getBytes())
                .build();

        kafkaTemplate.send(topicName, cloudEvent.toString());

        var consumerRecords = consumer.poll(Duration.ofSeconds(5));
        assertThat(consumerRecords).isNotNull();
    }
}
