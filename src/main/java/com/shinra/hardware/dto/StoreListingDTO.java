package com.shinra.hardware.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record StoreListingDTO(
        Long listingId,
        String storeName,
        String storeLogoUrl,
        BigDecimal price,
        String buyLink,
        Boolean inStock,
        OffsetDateTime lastUpdate
) {}
