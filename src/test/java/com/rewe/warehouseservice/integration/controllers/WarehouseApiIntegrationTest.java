package com.rewe.warehouseservice.integration.controllers;

import com.rewe.warehouseservice.WarehouseServiceApplication;
import com.rewe.warehouseservice.data.entities.Warehouse;
import com.rewe.warehouseservice.data.repositories.WarehouseRepository;
import com.rewe.warehouseservice.dtos.WarehouseDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = WarehouseServiceApplication.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WarehouseApiIntegrationTest {
    @LocalServerPort
    int randomServerPort;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @AfterEach
    void cleanupTestEntities() {
        warehouseRepository.deleteAll();
    }

    @Test
    void testAddWarehouse() throws URISyntaxException {
        URI uri = new URI("http://localhost:" + randomServerPort + "/api/warehouses");

        WarehouseDTO warehouseDTO = new WarehouseDTO();
        warehouseDTO.setWarehouseName("New Warehouse");
        warehouseDTO.setWarehouseIdentifier("1234567890123456");

        ResponseEntity<WarehouseDTO> responseEntity = restTemplate.postForEntity(uri, warehouseDTO, WarehouseDTO.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getWarehouseName()).isEqualTo("New Warehouse");

        Optional<Warehouse> warehouse = warehouseRepository.findById(responseEntity.getBody().getId());
        assertThat(warehouse.isPresent()).isTrue();
        assertThat(warehouse.get().getWarehouseName()).isEqualTo("New Warehouse");
    }

    @Test
    void testUpdateWarehouse() throws URISyntaxException {
        WarehouseDTO warehouseDTO = new WarehouseDTO();
        warehouseDTO.setWarehouseName("Warehouse to Update");

        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseName("Warehouse to Update");
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);

        URI uri = new URI("http://localhost:" + randomServerPort + "/api/warehouses/" + savedWarehouse.getId());

        warehouseDTO.setWarehouseName("Updated Warehouse");

        ResponseEntity<WarehouseDTO> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.PUT,
                new HttpEntity<>(warehouseDTO),
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getWarehouseName()).isEqualTo("Updated Warehouse");

        Optional<Warehouse> updatedWarehouse = warehouseRepository.findById(savedWarehouse.getId());

        assertThat(updatedWarehouse.get()).isNotNull();
        assertThat(updatedWarehouse.get().getWarehouseName()).isEqualTo("Updated Warehouse");
    }

    @Test
    void testDeleteWarehouse() throws URISyntaxException {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseName("Warehouse To Delete");
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);

        URI uri = new URI("http://localhost:" + randomServerPort + "/api/warehouses/" + savedWarehouse.getId());

        ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                uri,
                HttpMethod.DELETE,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isTrue();

        Optional<Warehouse> updatedWarehouse = warehouseRepository.findById(savedWarehouse.getId());
        assertThat(updatedWarehouse.isPresent()).isFalse();
    }

    @Test
    void testGetWarehouseById() throws URISyntaxException {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseName("Test Warehouse");
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);

        URI uri = new URI("http://localhost:" + randomServerPort + "/api/warehouses/" + savedWarehouse.getId());

        ResponseEntity<WarehouseDTO> responseEntity = restTemplate.getForEntity(uri, WarehouseDTO.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().getWarehouseName()).isEqualTo("Test Warehouse");

        Optional<Warehouse> result = warehouseRepository.findById(savedWarehouse.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getWarehouseName()).isEqualTo("Test Warehouse");
    }

    @Test
    void testGetAllWarehouses() throws URISyntaxException {
        Warehouse warehouse = new Warehouse();
        warehouse.setWarehouseName("Test Warehouse");
        Warehouse savedWarehouse = warehouseRepository.save(warehouse);

        URI uri = new URI("http://localhost:" + randomServerPort + "/api/warehouses");

        ResponseEntity<List> responseEntity = restTemplate.getForEntity(uri, List.class);

        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(responseEntity.getBody()).isNotNull();
        assertThat(responseEntity.getBody().size()).isEqualTo(1);

        List<Warehouse> bookingResult = warehouseRepository.findAll();
        assertThat(bookingResult.size()).isEqualTo(1);
        assertThat(bookingResult.getFirst().getWarehouseName()).isEqualTo("Test Warehouse");
    }
}
