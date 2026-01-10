package com.shinra.hardware.dto;

import java.math.BigDecimal;

public record PriceUpdateRequestDTO(
        BigDecimal price,
        Boolean inStock
) {}