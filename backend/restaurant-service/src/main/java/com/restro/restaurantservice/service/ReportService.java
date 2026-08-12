package com.restro.restaurantservice.service;

import com.restro.restaurantservice.domain.entity.AppUser;
import com.restro.restaurantservice.domain.entity.Invoice;
import com.restro.restaurantservice.domain.entity.MenuItem;
import com.restro.restaurantservice.domain.entity.OrderLine;
import com.restro.restaurantservice.domain.entity.RestaurantOrder;
import com.restro.restaurantservice.domain.enums.InvoiceStatus;
import com.restro.restaurantservice.web.dto.DashboardSummaryResponse;
import com.restro.restaurantservice.web.dto.MonthlyRevenuePoint;
import com.restro.restaurantservice.web.dto.RecentOrderResponse;
import com.restro.restaurantservice.web.dto.TopItemResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

	private static final ZoneId ZONE = ZoneId.systemDefault();
	private static final DateTimeFormatter MONTH_FORMAT = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH);

	private final MenuItemService menuItemService;
	private final CustomerService customerService;
	private final BillingService billingService;
	private final OrderService orderService;
	private final com.restro.restaurantservice.repository.MenuItemRepository menuItemRepository;
	private final com.restro.restaurantservice.repository.CustomerRepository customerRepository;
	private final com.restro.restaurantservice.repository.InvoiceRepository invoiceRepository;
	private final com.restro.restaurantservice.repository.RestaurantOrderRepository orderRepository;

	public ReportService(
			MenuItemService menuItemService,
			CustomerService customerService,
			BillingService billingService,
			OrderService orderService,
			com.restro.restaurantservice.repository.MenuItemRepository menuItemRepository,
			com.restro.restaurantservice.repository.CustomerRepository customerRepository,
			com.restro.restaurantservice.repository.InvoiceRepository invoiceRepository,
			com.restro.restaurantservice.repository.RestaurantOrderRepository orderRepository) {
		this.menuItemService = menuItemService;
		this.customerService = customerService;
		this.billingService = billingService;
		this.orderService = orderService;
		this.menuItemRepository = menuItemRepository;
		this.customerRepository = customerRepository;
		this.invoiceRepository = invoiceRepository;
		this.orderRepository = orderRepository;
	}

	@Transactional(readOnly = true)
	public DashboardSummaryResponse summary() {
		List<Invoice> invoices = invoiceRepository.findAll();
		List<RestaurantOrder> orders = orderRepository.findAll();
		BigDecimal totalRevenue = invoices.stream()
				.filter(invoice -> invoice.getStatus() == InvoiceStatus.PAID)
				.map(Invoice::getTotalAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		LocalDate today = LocalDate.now();
		BigDecimal todayRevenue = invoices.stream()
				.filter(invoice -> invoice.getStatus() == InvoiceStatus.PAID)
				.filter(invoice -> invoice.getPaidAt() != null && invoice.getPaidAt().atZone(ZONE).toLocalDate().equals(today))
				.map(Invoice::getTotalAmount)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		long totalOrders = orders.size();
		long todayOrders = orders.stream()
				.filter(order -> order.getCreatedAt() != null && order.getCreatedAt().atZone(ZONE).toLocalDate().equals(today))
				.count();
		long activeItems = menuItemRepository.count();
		long activeCustomers = customerRepository.count();
		long pendingInvoices = invoices.stream().filter(invoice -> invoice.getStatus() != InvoiceStatus.PAID).count();
		return new DashboardSummaryResponse(
				normalize(totalRevenue),
				normalize(todayRevenue),
				totalOrders,
				todayOrders,
				activeItems,
				activeCustomers,
				pendingInvoices);
	}

	@Transactional(readOnly = true)
	public List<MonthlyRevenuePoint> monthlyRevenue() {
		Map<YearMonth, BigDecimal> totals = new LinkedHashMap<>();
		List<Invoice> invoices = invoiceRepository.findAll();
		YearMonth current = YearMonth.now();
		for (int i = 5; i >= 0; i--) {
			totals.put(current.minusMonths(i), BigDecimal.ZERO);
		}
		for (Invoice invoice : invoices) {
			if (invoice.getStatus() != InvoiceStatus.PAID || invoice.getPaidAt() == null) {
				continue;
			}
			YearMonth month = YearMonth.from(invoice.getPaidAt().atZone(ZONE));
			if (totals.containsKey(month)) {
				totals.put(month, totals.get(month).add(invoice.getTotalAmount()));
			}
		}
		return totals.entrySet().stream()
				.map(entry -> new MonthlyRevenuePoint(
						MONTH_FORMAT.format(entry.getKey().atDay(1)),
						normalize(entry.getValue())))
				.toList();
	}

	@Transactional(readOnly = true)
	public List<TopItemResponse> topItems() {
		Map<Long, ItemAgg> aggregates = new HashMap<>();
		for (RestaurantOrder order : orderRepository.findAll()) {
			for (OrderLine line : order.getLines()) {
				MenuItem item = line.getMenuItem();
				ItemAgg agg = aggregates.computeIfAbsent(item.getId(), id -> new ItemAgg(item.getName(), item.getCategory()));
				agg.units += line.getQuantity();
				agg.revenue = agg.revenue.add(line.getLineTotal());
			}
		}
		return aggregates.entrySet().stream()
				.map(entry -> new TopItemResponse(
						entry.getKey(),
						entry.getValue().name,
						entry.getValue().category,
						entry.getValue().units,
						normalize(entry.getValue().revenue)))
				.sorted(Comparator.comparing(TopItemResponse::revenue).reversed())
				.limit(5)
				.toList();
	}

	@Transactional(readOnly = true)
	public List<RecentOrderResponse> recentOrders() {
		return orderRepository.findAllByOrderByCreatedAtDesc().stream()
				.limit(10)
				.map(order -> new RecentOrderResponse(
						order.getId(),
						order.getOrderNumber(),
						order.getTableNumber(),
						order.getCustomerName(),
						order.getLines().size(),
						normalize(order.getTotalAmount()),
						order.getStatus(),
						order.getCreatedAt()))
				.toList();
	}

	private BigDecimal normalize(BigDecimal value) {
		return value == null ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP) : value.setScale(2, RoundingMode.HALF_UP);
	}

	private static class ItemAgg {
		private final String name;
		private final String category;
		private long units;
		private BigDecimal revenue = BigDecimal.ZERO;

		private ItemAgg(String name, String category) {
			this.name = name;
			this.category = category;
		}
	}
}
