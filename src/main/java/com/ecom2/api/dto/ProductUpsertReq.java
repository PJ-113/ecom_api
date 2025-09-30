package com.ecom2.api.dto;

import java.math.BigDecimal;

public record ProductUpsertReq(
        String name,
        String description,
        BigDecimal price,
        String imagePath,
        Boolean active      // optional
) {}
