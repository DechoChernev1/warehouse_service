package com.rewe.warehouseservice.data.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class ProducedMessages extends BaseEntity {
    @Column(unique=true)
    private UUID msgKey;
    private String status;
    private String payload;
    @Size(min = 6, max = 6)
    @CreationTimestamp
    private Instant created;
    @Size(min = 6, max = 6)
    @UpdateTimestamp
    private Instant updated;
}
