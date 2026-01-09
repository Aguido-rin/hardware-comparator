package com.shinra.hardware.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Data
@Entity
@Table(name = "categories", schema = "comp_hard_sys")
public class Category {

    //Es Necesario?? XD
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer id;

    // Nombre de la categoría
    @Column(nullable = false, unique = true)
    private String name;

    // Slug amigable para URLs
    @Column(nullable = false, unique = true)
    private String slug;

    // updatable=false evita que se modifique la fecha de creación original
    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;
}
