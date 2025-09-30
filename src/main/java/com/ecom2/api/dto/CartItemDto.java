package com.ecom2.api.dto;

import java.math.BigDecimal;

public record CartItemDto(
	    Long id,
	    Long productId,
	    String productName,
	    int quantity,
	    BigDecimal unitPrice,
	    BigDecimal lineTotal,
	    String imagePath
	) {}
