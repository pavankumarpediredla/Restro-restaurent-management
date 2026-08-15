package com.restro.restaurantservice.web.controller;

import com.restro.restaurantservice.service.ReportService;
import com.restro.restaurantservice.web.dto.DashboardSummaryResponse;
import com.restro.restaurantservice.web.dto.MonthlyRevenuePoint;
import com.restro.restaurantservice.web.dto.RecentOrderResponse;
import com.restro.restaurantservice.web.dto.TopItemResponse;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('OWNER', 'ADMIN', 'MANAGER')")
public class ReportController {

	private final ReportService reportService;

	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}

	@GetMapping("/summary")
	public DashboardSummaryResponse summary() {
		return reportService.summary();
	}

	@GetMapping("/monthly-revenue")
	public List<MonthlyRevenuePoint> monthlyRevenue() {
		return reportService.monthlyRevenue();
	}

	@GetMapping("/top-items")
	public List<TopItemResponse> topItems() {
		return reportService.topItems();
	}

	@GetMapping("/recent-orders")
	public List<RecentOrderResponse> recentOrders() {
		return reportService.recentOrders();
	}
}
