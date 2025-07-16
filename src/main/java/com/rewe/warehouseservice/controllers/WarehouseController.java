package com.rewe.warehouseservice.controllers;

import com.rewe.warehouseservice.dtos.WarehouseDTO;
import com.rewe.warehouseservice.services.WarehouseService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {
    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @PostMapping
    public ResponseEntity<WarehouseDTO> addWarehouse(@Valid @RequestBody WarehouseDTO warehouseDTO) throws IOException {
        WarehouseDTO savedWarehouse = warehouseService.saveWarehouse(warehouseDTO);
        return new ResponseEntity<>(savedWarehouse, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseDTO> getWarehouseById(@PathVariable @Positive Integer id) {
        WarehouseDTO warehouseDTO = warehouseService.findWarehouseById(id);
        return ResponseEntity.ok(warehouseDTO);
    }

    @GetMapping
    public ResponseEntity<List<WarehouseDTO>> getAllWarehouses() {
        List<WarehouseDTO> warehouseDTOS = warehouseService.findAllWarehouses();
        return ResponseEntity.ok(warehouseDTOS);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WarehouseDTO> updateWarehouse(@PathVariable @Positive Integer id,
                                                        @Valid @RequestBody WarehouseDTO warehouseDTO) throws IOException {
        WarehouseDTO updatedWarehouse = warehouseService.updateWarehouse(id, warehouseDTO);
        return ResponseEntity.ok(updatedWarehouse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteWarehouse(@PathVariable @Positive Integer id) throws IOException {
        return ResponseEntity.ok(warehouseService.deleteWarehouse(id));
    }
}
