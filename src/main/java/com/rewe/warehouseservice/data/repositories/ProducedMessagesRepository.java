package com.rewe.warehouseservice.data.repositories;

import com.rewe.warehouseservice.data.entities.ProducedMessages;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProducedMessagesRepository extends JpaRepository<ProducedMessages, Integer> {
}
