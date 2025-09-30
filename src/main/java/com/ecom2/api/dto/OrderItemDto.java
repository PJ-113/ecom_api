package com.ecom2.api.dto;

import java.math.BigDecimal;
import java.util.List;
public record OrderItemDto(Long productId, String productName, int quantity, BigDecimal price, List<String> redeemCodes ) {}
