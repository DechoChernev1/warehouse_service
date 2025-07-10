package com.rewe.warehouseservice.dtos;

import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class WarehouseDTO {
    private Integer id;
    @Size(max = 50)
    private String warehouseName;
    @Size(min = 16, max = 16)
    private String warehouseIdentifier;
    private Instant created;
    private Instant updated;
}
