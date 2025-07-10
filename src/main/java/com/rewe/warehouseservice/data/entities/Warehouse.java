package com.rewe.warehouseservice.data.entities;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class Warehouse extends BaseEntity {
    private String warehouseName;
    private String warehouseIdentifier;
    @CreationTimestamp
    private Instant created;
    @UpdateTimestamp
    private Instant updated;
}
