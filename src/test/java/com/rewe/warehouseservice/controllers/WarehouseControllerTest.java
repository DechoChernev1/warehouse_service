package com.rewe.warehouseservice.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rewe.warehouseservice.dtos.WarehouseDTO;
import com.rewe.warehouseservice.services.WarehouseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WarehouseController.class)
class WarehouseControllerTest {
    @MockitoBean
    private WarehouseService warehouseService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private WarehouseDTO warehouseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        warehouseDTO = new WarehouseDTO();
        warehouseDTO.setId(1);
        warehouseDTO.setWarehouseName("test");
        warehouseDTO.setWarehouseIdentifier("1234567890123456");
    }

    @Test
    void testAddBooking() throws Exception {
        when(warehouseService.saveWarehouse(any(WarehouseDTO.class))).thenReturn(warehouseDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/warehouses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(warehouseDTO)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(warehouseDTO.getId()), Integer.class));
    }

    @Test
    void testAddBookingWithWrongIdentifier() throws Exception {
        warehouseDTO.setWarehouseIdentifier("test");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/warehouses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(warehouseDTO)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title", is("Constraint Violation Exception"), String.class));
    }

    @Test
    void testGetWarehouseById() throws Exception {
        when(warehouseService.findWarehouseById(1)).thenReturn(warehouseDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/warehouses/{id}", warehouseDTO.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(warehouseDTO.getId()), Integer.class));
    }

    @Test
    void testGetAllWarehouses() throws Exception {
        when(warehouseService.findAllWarehouses()).thenReturn(List.of(warehouseDTO));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/warehouses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]id", is(warehouseDTO.getId()), Integer.class));
    }

    @Test
    void testUpdateWarehouse() throws Exception {
        when(warehouseService.updateWarehouse(eq(1), any(WarehouseDTO.class))).thenReturn(warehouseDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/warehouses/{id}", warehouseDTO.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(warehouseDTO)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(warehouseDTO.getId()), Integer.class))
                .andExpect(jsonPath("$.warehouseName", is(warehouseDTO.getWarehouseName()), String.class));
    }

    @Test
    void testDeleteWarehouse() throws Exception {
        when(warehouseService.deleteWarehouse(1)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/api/warehouses/{id}", warehouseDTO.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }
}