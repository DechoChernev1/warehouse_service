package com.rewe.warehouseservice.services;

import com.rewe.warehouseservice.dtos.WarehouseDTO;

import java.io.IOException;
import java.util.List;

public interface WarehouseService {
    List<WarehouseDTO> findAllWarehouses();

    WarehouseDTO findWarehouseById(Integer id);

    WarehouseDTO saveWarehouse(WarehouseDTO warehouseDTO) throws IOException;

    WarehouseDTO updateWarehouse(Integer id, WarehouseDTO warehouseDTO) throws IOException;

    boolean deleteWarehouse(Integer id) throws IOException;
}
