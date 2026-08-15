package com.restro.restaurantservice.web.dto;

import java.math.BigDecimal;

public record DashboardSummaryResponse(
		BigDecimal totalRevenue,
		BigDecimal todayRevenue,
		long totalOrders,
		long todayOrders,
		long activeItems,
		long activeCustomers,
		long pendingInvoices) {
}
