package com.restro.restaurantservice.web.dto;

import com.restro.restaurantservice.domain.enums.PriceCycle;
import java.math.BigDecimal;

public record OrderLineResponse(
		Long menuItemId,
		String menuItemName,
		PriceCycle priceCycle,
		int quantity,
		BigDecimal unitPrice,
		BigDecimal lineTotal) {
}
