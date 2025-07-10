package com.rewe.warehouseservice.services;

import com.rewe.warehouseservice.dtos.WarehouseDTO;

import java.util.List;
import java.util.Optional;

public interface WarehouseService {
    List<WarehouseDTO> findAllWarehouses();

    WarehouseDTO findWarehouseById(Integer id);

    WarehouseDTO saveWarehouse(WarehouseDTO warehouseDTO);

    WarehouseDTO updateWarehouse(Integer id, WarehouseDTO warehouseDTO);

    boolean deleteWarehouse(Integer id);
}
