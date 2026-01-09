package com.shinra.hardware.dto;

import java.util.Map;

public record ProductDTO(
        Long id,
        String categoryName,
        String modelName,
        String brand,
        Map<String, Object> techSpecs,
        String imageUrl,
        Double bestPrice
) {}