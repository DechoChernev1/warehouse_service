package com.rewe.warehouseservice.services.impl;

import com.rewe.warehouseservice.config.WarehouseMapper;
import com.rewe.warehouseservice.data.entities.ProducedMessages;
import com.rewe.warehouseservice.data.entities.Warehouse;
import com.rewe.warehouseservice.data.repositories.WarehouseRepository;
import com.rewe.warehouseservice.dtos.WarehouseDTO;
import com.rewe.warehouseservice.enums.ProducedMessageType;
import com.rewe.warehouseservice.services.WarehouseService;
import com.rewe.warehouseservice.services.kafka.KafkaSenderService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;
    private final KafkaSenderService kafkaSenderService;

    public WarehouseServiceImpl(
            WarehouseRepository warehouseRepository,
            WarehouseMapper warehouseMapper,
            KafkaSenderService kafkaSenderService) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
        this.kafkaSenderService = kafkaSenderService;
    }

    @Override
    public List<WarehouseDTO> findAllWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(warehouseMapper::warehouseToWarehouseDTO)
                .toList();
    }

    @Override
    public WarehouseDTO findWarehouseById(Integer id) {
        return warehouseRepository.findById(id)
                .map(warehouseMapper::warehouseToWarehouseDTO)
                .orElseThrow(EntityExistsException::new);
    }

    @Override
    public WarehouseDTO saveWarehouse(WarehouseDTO warehouseDTO) throws IOException {
        Warehouse warehouseEntity = warehouseMapper.warehouseDTOToWarehouse(warehouseDTO);
        Warehouse savedWarehouse = warehouseRepository.save(warehouseEntity);
        log.info("Warehouse saved successfully");
        sendKafkaMessageWithWarehouse(savedWarehouse, "com.rewe.warehouse.createEvent");
        return warehouseMapper.warehouseToWarehouseDTO(savedWarehouse);
    }

    @Override
    public WarehouseDTO updateWarehouse(Integer id, WarehouseDTO warehouseDTO) throws IOException {
        Optional<Warehouse> existingWarehouse = warehouseRepository.findById(id);
        if (existingWarehouse.isPresent()) {
            Warehouse warehouseToUpdate = existingWarehouse.get();
            warehouseToUpdate.setWarehouseName(warehouseDTO.getWarehouseName());
            warehouseToUpdate.setWarehouseIdentifier(warehouseDTO.getWarehouseIdentifier());
            Warehouse updatedWarehouse = warehouseRepository.save(warehouseToUpdate);

            sendKafkaMessageWithWarehouse(updatedWarehouse, "com.rewe.warehouse.updateEvent");
            return warehouseMapper.warehouseToWarehouseDTO(updatedWarehouse);
        }
        throw new EntityNotFoundException("Warehouse not found for id: " + id);
    }

    @Override
    public boolean deleteWarehouse(Integer id) throws IOException {
        warehouseRepository.deleteById(id);
        sendKafkaMessage(id.toString(), "com.rewe.warehouse.deleteEvent");
        return !warehouseRepository.existsById(id);
    }

    private void sendKafkaMessageWithWarehouse(Warehouse warehouse, String cloudEventType) throws IOException {
        ProducedMessages producedMessages = new ProducedMessages();
        producedMessages.setMsgKey(UUID.randomUUID());
        producedMessages.setStatus(ProducedMessageType.PENDING.toString());
        producedMessages.setPayload(warehouse.toString());
        kafkaSenderService.send(producedMessages, cloudEventType);
    }

    private void sendKafkaMessage(String payload, String cloudEventType) throws IOException {
        ProducedMessages producedMessages = new ProducedMessages();
        producedMessages.setMsgKey(UUID.randomUUID());
        producedMessages.setStatus(ProducedMessageType.PENDING.toString());
        producedMessages.setPayload(payload);
        kafkaSenderService.send(producedMessages, cloudEventType);
    }
}
