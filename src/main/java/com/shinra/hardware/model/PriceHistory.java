package com.shinra.hardware.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "price_history", schema = "comp_hard_sys")
public class PriceHistory {

    // Es necesario?? XD
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    // Relación Many-to-One con StoreListing
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "listing_id", nullable = false)
    private StoreListing listing;

    // Precio registrado en el historial
    @Column(nullable = false)
    private BigDecimal price;

    // Fecha y hora en que se registró el precio
    @Column(name = "recorded_at", insertable = false, updatable = false)
    private OffsetDateTime recordedAt;
}