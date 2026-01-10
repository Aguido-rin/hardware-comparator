package com.shinra.hardware.dto;

import java.math.BigDecimal;

public record DiscoveredProductDTO(
        String title,
        String url,
        String imageUrl,
        BigDecimal price,
        String storeName,
        String categorySlug
) {}