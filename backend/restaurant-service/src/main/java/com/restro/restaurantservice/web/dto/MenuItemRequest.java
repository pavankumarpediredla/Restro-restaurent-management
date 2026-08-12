package com.restro.restaurantservice.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record MenuItemRequest(
		@NotBlank String name,
		String description,
		String category,
		Boolean active,
		@Valid @NotEmpty List<ItemPriceRequest> prices) {
}
