package com.rewe.warehouseservice.services.impl;

import com.rewe.warehouseservice.config.WarehouseMapper;
import com.rewe.warehouseservice.config.WarehouseMapperImpl;
import com.rewe.warehouseservice.data.entities.Warehouse;
import com.rewe.warehouseservice.data.repositories.WarehouseRepository;
import com.rewe.warehouseservice.dtos.WarehouseDTO;
import com.rewe.warehouseservice.services.kafka.KafkaSenderService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WarehouseServiceImplTest {
    private final WarehouseMapper warehouseMapper = Mockito.spy(WarehouseMapperImpl.class);
    private final WarehouseRepository warehouseRepository = Mockito.mock(WarehouseRepository.class);
    private final KafkaSenderService kafkaSenderService = Mockito.mock(KafkaSenderService.class);
    private final WarehouseServiceImpl warehouseService = new WarehouseServiceImpl(warehouseRepository, warehouseMapper, kafkaSenderService);

    private Warehouse warehouse;
    private WarehouseDTO warehouseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        warehouse = new Warehouse();
        warehouse.setId(1);
        warehouse.setWarehouseName("Rewe 1");
        warehouse.setWarehouseIdentifier(UUID.randomUUID().toString());
        warehouse.setCreated(Instant.now());
        warehouse.setUpdated(Instant.now());
        warehouseDTO = warehouseMapper.warehouseToWarehouseDtо(warehouse);
    }

    @Test
    void findAllWarehouses() {
        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));

        List<WarehouseDTO> result = warehouseService.findAllWarehouses();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(warehouseDTO.getId(), result.get(0).getId());
        verify(warehouseRepository).findAll();
        verify(warehouseMapper, atLeast(1)).warehouseToWarehouseDtо(warehouse);
    }

    @Test
    void findWarehouseById() {
        when(warehouseRepository.findById(1)).thenReturn(Optional.of(warehouse));

        WarehouseDTO result = warehouseService.findWarehouseById(1);

        assertNotNull(result);
        assertEquals(warehouseDTO.getId(), result.getId());
        verify(warehouseRepository).findById(1);
        verify(warehouseMapper, atLeast(1)).warehouseToWarehouseDtо(warehouse);
    }

    @Test
    void saveWarehouse() {
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        WarehouseDTO result = warehouseService.saveWarehouse(warehouseDTO);

        assertNotNull(result);
        assertEquals(warehouseDTO.getId(), result.getId());
        assertEquals(warehouseDTO.getWarehouseName(), result.getWarehouseName());
        verify(warehouseRepository, times(1)).save(any(Warehouse.class));
        verify(warehouseMapper, atLeast(1)).warehouseToWarehouseDtо(warehouse);
    }

    @Test
    void updateWarehouse() {
        when(warehouseRepository.findById(1)).thenReturn(Optional.of(warehouse));
        when(warehouseRepository.save(any(Warehouse.class))).thenReturn(warehouse);

        WarehouseDTO updatedDetails = new WarehouseDTO();
        updatedDetails.setId(1);
        updatedDetails.setWarehouseName("Updated");

        WarehouseDTO result = warehouseService.updateWarehouse(1, updatedDetails);

        assertNotNull(result);
        assertEquals("Updated", result.getWarehouseName());
        verify(warehouseRepository).findById(1);
        verify(warehouseRepository).save(warehouse);
        verify(warehouseMapper, atLeast(1)).warehouseToWarehouseDtо(warehouse);
    }

    @Test
    void deleteWarehouse() {
        when(warehouseRepository.existsById(1)).thenReturn(false);

        boolean result = warehouseService.deleteWarehouse(1);

        assertTrue(result);
        verify(warehouseRepository).deleteById(1);
    }
}