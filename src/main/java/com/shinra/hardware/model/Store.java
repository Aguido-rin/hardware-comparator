package com.shinra.hardware.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "stores", schema = "comp_hard_sys")
public class Store {

    // Es necesario?? XD
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Integer id;

    // Nombre de la tienda
    @Column(nullable = false, unique = true)
    private String name;

    // URL base de la tienda
    @Column(name = "base_url", nullable = false)
    private String baseUrl;

    // Parámetro de afiliado para la tienda
    @Column(name = "affiliate_param")
    private String affiliateParam;

    // URL del logo de la tienda
    @Column(name = "logo_url")
    private String logoUrl;

    // Frecuencia de scraping en horas
    @Column(name = "scrape_frequency_hours")
    private Integer scrapeFrequencyHours;
}