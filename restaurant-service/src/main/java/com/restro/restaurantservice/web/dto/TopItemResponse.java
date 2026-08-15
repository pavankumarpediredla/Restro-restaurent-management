package com.restro.restaurantservice.web.dto;

import java.math.BigDecimal;

public record TopItemResponse(
		Long menuItemId,
		String item,
		String category,
		long unitsSold,
		BigDecimal revenue) {
}
