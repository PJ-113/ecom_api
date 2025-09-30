package com.ecom2.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDto(Long id, Long userId, LocalDateTime createdAt, BigDecimal total, String status
		, List<OrderItemDto> items) {}
