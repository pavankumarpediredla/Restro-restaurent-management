package com.restro.restaurantservice.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record OrderCreateRequest(
		Long customerId,
		String customerName,
		String customerPhone,
		String customerEmail,
		String customerAddress,
		String tableNumber,
		String notes,
		@Valid @NotEmpty List<OrderLineRequest> items) {
}
