package com.rewe.warehouseservice;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@SpringBootApplication
public class WarehouseServiceApplication {
	@Value("${service.topic.name}")
	private String topicName;

	public static void main(String[] args) {
		SpringApplication.run(WarehouseServiceApplication.class, args);
	}

	@Bean
	public NewTopic topic() {
		return TopicBuilder.name(topicName)
				.partitions(10)
				.replicas(1)
				.build();
	}
}
