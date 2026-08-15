package com.restro.restaurantservice.web.dto;

import java.time.Instant;
import java.util.List;

public record MenuItemResponse(
		Long id,
		String name,
		String description,
		String category,
		boolean active,
		List<ItemPriceResponse> prices,
		Instant createdAt,
		Instant updatedAt) {
}
