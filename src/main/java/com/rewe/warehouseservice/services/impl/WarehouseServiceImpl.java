package com.rewe.warehouseservice.services.impl;

import com.rewe.warehouseservice.config.WarehouseMapper;
import com.rewe.warehouseservice.data.entities.Warehouse;
import com.rewe.warehouseservice.data.repositories.WarehouseRepository;
import com.rewe.warehouseservice.dtos.WarehouseDTO;
import com.rewe.warehouseservice.services.WarehouseService;
import com.rewe.warehouseservice.services.kafka.KafkaSenderService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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
        log.info("Before send kafka msg");
        kafkaSenderService.send("warehouses");
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
    public WarehouseDTO saveWarehouse(WarehouseDTO warehouseDTO) {
        Warehouse warehouseEntity = warehouseMapper.warehouseDTOToWarehouse(warehouseDTO);
        Warehouse savedWarehouse = warehouseRepository.save(warehouseEntity);
        return warehouseMapper.warehouseToWarehouseDTO(savedWarehouse);
    }

    @Override
    public WarehouseDTO updateWarehouse(Integer id, WarehouseDTO warehouseDTO) {
        Optional<Warehouse> existingWarehouse = warehouseRepository.findById(id);
        if (existingWarehouse.isPresent()) {
            Warehouse warehouseToUpdate = existingWarehouse.get();
            warehouseToUpdate.setWarehouseName(warehouseDTO.getWarehouseName());
            warehouseToUpdate.setWarehouseIdentifier(warehouseDTO.getWarehouseIdentifier());
            Warehouse updatedWarehouse = warehouseRepository.save(warehouseToUpdate);
            return warehouseMapper.warehouseToWarehouseDTO(updatedWarehouse);
        }
        throw new EntityNotFoundException("Warehouse not found for id: " + id);
    }

    @Override
    public boolean deleteWarehouse(Integer id) {
        warehouseRepository.deleteById(id);
        return !warehouseRepository.existsById(id);
    }
}
