package com.restro.restaurantservice.web.dto;

import java.math.BigDecimal;

public record InvoiceCreateRequest(
		Long orderId,
		BigDecimal taxAmount,
		BigDecimal discountAmount,
		String notes) {
}
