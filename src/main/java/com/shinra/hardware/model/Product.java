package com.shinra.hardware.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
@Entity
@Table(name = "products", schema = "comp_hard_sys")
public class Product {

    // Es necesario?? XD
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    // Relación Many-to-One con Category
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    // Nombre del modelo del producto
    @Column(name = "model_name", nullable = false)
    private String modelName;

    // Marca del producto
    @Column(name = "brand", nullable = false)
    private String brand;

    // Especificaciones almacenadas como JSONB para que googgle la lea
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "tech_specs", columnDefinition = "jsonb")
    private Map<String, Object> techSpecs;

    // URL de la imagen del producto
    @Column(name = "image_url")
    private String imageUrl;

    // Por defecto, un producto es activo al crearlo
    @Column(name = "is_active")
    private Boolean isActive = true;

    // updatable=false evita que se modifique la fecha de creación original
    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
