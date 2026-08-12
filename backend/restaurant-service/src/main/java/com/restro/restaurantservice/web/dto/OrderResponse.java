package com.restro.restaurantservice.web.dto;

import com.restro.restaurantservice.domain.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
		Long id,
		String orderNumber,
		OrderStatus status,
		Long customerId,
		String customerName,
		String customerPhone,
		String tableNumber,
		String notes,
		BigDecimal totalAmount,
		Instant createdAt,
		Instant acceptedAt,
		String createdBy,
		String acceptedBy,
		List<OrderLineResponse> items) {
}
