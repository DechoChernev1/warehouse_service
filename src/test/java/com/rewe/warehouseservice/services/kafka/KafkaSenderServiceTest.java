package com.rewe.warehouseservice.services.kafka;

import com.rewe.warehouseservice.data.entities.ProducedMessages;
import com.rewe.warehouseservice.data.repositories.ProducedMessagesRepository;
import com.rewe.warehouseservice.enums.ProducedMessageType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.core.KafkaTemplate;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class KafkaSenderServiceTest {
    private final KafkaTemplate template = Mockito.mock(KafkaTemplate.class);

    private final KafkaSenderService kafkaSenderService;

    private final ProducedMessagesRepository messagesRepository = Mockito.mock(ProducedMessagesRepository.class);

    KafkaSenderServiceTest() {
        this.kafkaSenderService = new KafkaSenderService(template, messagesRepository, "topicName");
    }

    @Test
    void send() throws IOException {
        when(template.send(anyString(), anyString())).thenReturn(CompletableFuture.completedFuture(null));
        ProducedMessages producedMessages = new ProducedMessages();
        producedMessages.setStatus(ProducedMessageType.PENDING.toString());
        kafkaSenderService.send(producedMessages, "com.rewe.warehouses.createEvent");

        producedMessages.setStatus(ProducedMessageType.SENT.toString());
        verify(template).send(eq("topicName"), anyString());
        verify(messagesRepository, atLeast(1)).save(producedMessages);
    }

    @Test
    void sendWithException() throws IOException {
        when(template.send(anyString(), anyString())).thenReturn(CompletableFuture.failedFuture(new Throwable()));
        ProducedMessages producedMessages = new ProducedMessages();
        producedMessages.setStatus(ProducedMessageType.PENDING.toString());
        kafkaSenderService.send(producedMessages, "com.rewe.warehouses.createEvent");

        producedMessages.setStatus(ProducedMessageType.FAILED.toString());
        verify(template).send(eq("topicName"), anyString());
        verify(messagesRepository, atLeast(1)).save(producedMessages);
    }
}