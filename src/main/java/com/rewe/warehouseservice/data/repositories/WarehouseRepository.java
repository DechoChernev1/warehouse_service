package com.rewe.warehouseservice.data.repositories;

import com.rewe.warehouseservice.data.entities.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<Warehouse, Integer> {
}
