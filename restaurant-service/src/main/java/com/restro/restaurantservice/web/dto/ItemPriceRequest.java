package com.restro.restaurantservice.web.dto;

import com.restro.restaurantservice.domain.enums.PriceCycle;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record ItemPriceRequest(
		@NotNull PriceCycle cycle,
		@NotNull @Positive BigDecimal amount) {
}
