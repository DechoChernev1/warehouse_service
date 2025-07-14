package com.rewe.warehouseservice.config;

import com.rewe.warehouseservice.data.entities.Warehouse;
import com.rewe.warehouseservice.dtos.WarehouseDTO;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseMapperTest {
    private final WarehouseMapper warehouseMapper = Mappers.getMapper(WarehouseMapper.class);

    @Test
    void testMapping_givenWarehouse_shouldBeMappedToWarehouseDTO() {
        // Given
        var warehouse = Warehouse.builder()
                .id(1)
                .warehouseIdentifier("WH1")
                .warehouseName("Warehouse 1")
                .created(Instant.now())
                .updated(Instant.now())
                .build();

        // When
        WarehouseDTO result = warehouseMapper.warehouseToWarehouseDTO(warehouse);

        // Then
        assertEquals(warehouse.getId(), result.getId());
        assertEquals(warehouse.getWarehouseIdentifier(), result.getWarehouseIdentifier());
        assertEquals(warehouse.getWarehouseName(), result.getWarehouseName());
        assertEquals(warehouse.getCreated(), result.getCreated());
        assertEquals(warehouse.getUpdated(), result.getUpdated());
    }
}