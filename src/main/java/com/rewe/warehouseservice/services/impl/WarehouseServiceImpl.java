package com.rewe.warehouseservice.services.impl;

import com.rewe.warehouseservice.config.WarehouseMapper;
import com.rewe.warehouseservice.data.entities.Warehouse;
import com.rewe.warehouseservice.data.repositories.WarehouseRepository;
import com.rewe.warehouseservice.dtos.WarehouseDTO;
import com.rewe.warehouseservice.services.WarehouseService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class WarehouseServiceImpl implements WarehouseService {
    private final WarehouseRepository warehouseRepository;
    private final WarehouseMapper warehouseMapper;

    public WarehouseServiceImpl(WarehouseRepository warehouseRepository, WarehouseMapper warehouseMapper) {
        this.warehouseRepository = warehouseRepository;
        this.warehouseMapper = warehouseMapper;
    }

    @Override
    public List<WarehouseDTO> findAllWarehouses() {
        return warehouseRepository.findAll()
                .stream()
                .map(warehouseMapper::warehouseToWarehouseDtо)
                .toList();
    }

    @Override
    public WarehouseDTO findWarehouseById(Integer id) {
        return warehouseRepository.findById(id)
                .map(warehouseMapper::warehouseToWarehouseDtо)
                .orElseThrow(EntityExistsException::new);
    }

    @Override
    public WarehouseDTO saveWarehouse(WarehouseDTO warehouseDTO) {
        Warehouse warehouseEntity = warehouseMapper.warehouseDtoToWarehouse(warehouseDTO);
        Warehouse savedWarehouse = warehouseRepository.save(warehouseEntity);
        return warehouseMapper.warehouseToWarehouseDtо(savedWarehouse);
    }

    @Override
    public WarehouseDTO updateWarehouse(Integer id, WarehouseDTO warehouseDTO) {
        Optional<Warehouse> existingWarehouse = warehouseRepository.findById(id);
        if (existingWarehouse.isPresent()) {
            Warehouse warehouseToUpdate = existingWarehouse.get();
            warehouseToUpdate.setWarehouseName(warehouseDTO.getWarehouseName());
            warehouseToUpdate.setWarehouseIdentifier(warehouseDTO.getWarehouseIdentifier());
            Warehouse updatedWarehouse = warehouseRepository.save(warehouseToUpdate);
            return warehouseMapper.warehouseToWarehouseDtо(updatedWarehouse);
        }
        throw new EntityNotFoundException("Warehouse not found for id: " + id);
    }

    @Override
    public boolean deleteWarehouse(Integer id) {
        warehouseRepository.deleteById(id);
        return !warehouseRepository.existsById(id);
    }
}
