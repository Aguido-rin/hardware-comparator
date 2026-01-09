package com.shinra.hardware.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "store_listings", schema = "comp_hard_sys")
public class StoreListing {

    // Es necesario?? XD
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "listing_id")
    private Long id;

    // Relación Many-to-One con Product
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // Relación Many-to-One con Store
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    // URL del producto en la tienda
    @Column(name = "url_source", nullable = false)
    private String urlSource;

    // Precio actual del producto en la tienda
    @Column(name = "current_price")
    private BigDecimal currentPrice;

    // Fecha y hora de la última verificación del stock y precio
    @Column(name = "last_checked_at")
    private OffsetDateTime lastCheckedAt;

    // Indica si el producto está en stock
    @Column(name = "is_in_stock")
    private Boolean isInStock;

    // Enlace de afiliado generado para el producto en la tienda
    @Column(name = "affiliate_link_generated")
    private String affiliateLinkGenerated;
}