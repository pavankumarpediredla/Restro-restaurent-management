package com.restro.restaurantservice.web.dto;

import java.math.BigDecimal;

public record MonthlyRevenuePoint(
		String month,
		BigDecimal revenue) {
}
