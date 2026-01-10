package com.shinra.hardware.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record StoreListingDTO(
        Long listingId,
        String storeName,
        String storeLogoUrl,
        BigDecimal price,
        String buyLink,     // Enlace para el usuario (puede ser afiliado)
        String urlSource,
        Boolean inStock,
        OffsetDateTime lastUpdate
) {}