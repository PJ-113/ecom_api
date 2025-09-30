package com.ecom2.api.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProductDto(
        Long id,
        String name,
        String description,
        BigDecimal price,
        long stock,
        @JsonProperty("image_path") String imagePath,
        Boolean active 
) {}
