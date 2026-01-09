package com.shinra.hardware.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record PriceHistoryPointDTO(
        OffsetDateTime date,
        BigDecimal price
) {}
