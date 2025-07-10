package com.rewe.warehouseservice.config;

import com.rewe.warehouseservice.data.entities.Warehouse;
import com.rewe.warehouseservice.dtos.WarehouseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface WarehouseMapper {
    WarehouseDTO warehouseToWarehouseDtо(Warehouse warehouse);
    Warehouse warehouseDtoToWarehouse(WarehouseDTO warehouseDTO);
}
