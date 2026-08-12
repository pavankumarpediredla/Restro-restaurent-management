package com.restro.restaurantservice.web.dto;

import com.restro.restaurantservice.domain.enums.OrderStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record RecentOrderResponse(
		Long id,
		String orderNumber,
		String tableNumber,
		String customerName,
		int items,
		BigDecimal totalAmount,
		OrderStatus status,
		Instant createdAt) {
}
