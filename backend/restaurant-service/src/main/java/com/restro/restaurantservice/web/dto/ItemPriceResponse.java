package com.restro.restaurantservice.web.dto;

import com.restro.restaurantservice.domain.enums.PriceCycle;
import java.math.BigDecimal;

public record ItemPriceResponse(
		PriceCycle cycle,
		BigDecimal amount) {
}
