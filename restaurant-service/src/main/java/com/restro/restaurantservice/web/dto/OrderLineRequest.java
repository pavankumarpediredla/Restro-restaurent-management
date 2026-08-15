package com.restro.restaurantservice.web.dto;

import com.restro.restaurantservice.domain.enums.PriceCycle;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record OrderLineRequest(
		@NotNull Long menuItemId,
		PriceCycle priceCycle,
		@Positive int quantity) {
}
