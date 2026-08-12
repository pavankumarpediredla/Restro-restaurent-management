package com.restro.restaurantservice.web.dto;

import com.restro.restaurantservice.domain.enums.InvoiceStatus;
import java.math.BigDecimal;
import java.time.Instant;

public record InvoiceResponse(
		Long id,
		String invoiceNumber,
		Long orderId,
		String orderNumber,
		Long customerId,
		String customerName,
		InvoiceStatus status,
		BigDecimal subtotal,
		BigDecimal taxAmount,
		BigDecimal discountAmount,
		BigDecimal totalAmount,
		Instant createdAt,
		Instant paidAt,
		String notes) {
}
