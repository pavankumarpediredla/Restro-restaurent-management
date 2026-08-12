package com.restro.restaurantservice.service;

import com.restro.restaurantservice.domain.entity.AppUser;
import com.restro.restaurantservice.domain.entity.Customer;
import com.restro.restaurantservice.domain.entity.ItemPrice;
import com.restro.restaurantservice.domain.entity.MenuItem;
import com.restro.restaurantservice.domain.entity.OrderLine;
import com.restro.restaurantservice.domain.entity.RestaurantOrder;
import com.restro.restaurantservice.domain.enums.OrderStatus;
import com.restro.restaurantservice.domain.enums.PriceCycle;
import com.restro.restaurantservice.domain.exception.BusinessRuleException;
import com.restro.restaurantservice.domain.exception.ResourceNotFoundException;
import com.restro.restaurantservice.repository.AppUserRepository;
import com.restro.restaurantservice.repository.MenuItemRepository;
import com.restro.restaurantservice.repository.RestaurantOrderRepository;
import com.restro.restaurantservice.service.CustomerService;
import com.restro.restaurantservice.web.dto.OrderCreateRequest;
import com.restro.restaurantservice.web.dto.OrderLineRequest;
import com.restro.restaurantservice.web.dto.OrderLineResponse;
import com.restro.restaurantservice.web.dto.OrderResponse;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

	private final RestaurantOrderRepository orderRepository;
	private final MenuItemRepository menuItemRepository;
	private final AppUserRepository userRepository;
	private final CustomerService customerService;

	public OrderService(
			RestaurantOrderRepository orderRepository,
			MenuItemRepository menuItemRepository,
			AppUserRepository userRepository,
			CustomerService customerService) {
		this.orderRepository = orderRepository;
		this.menuItemRepository = menuItemRepository;
		this.userRepository = userRepository;
		this.customerService = customerService;
	}

	@Transactional
	public OrderResponse create(String username, OrderCreateRequest request) {
		AppUser creator = findUser(username);
		Customer customer = customerService.resolveForOrder(
				request.customerId(),
				request.customerName(),
				request.customerPhone(),
				request.customerEmail(),
				request.customerAddress());
		RestaurantOrder order = new RestaurantOrder();
		order.setOrderNumber(generateOrderNumber());
		order.setStatus(OrderStatus.NEW);
		order.setCreatedBy(creator);
		order.setCustomer(customer);
		order.setTableNumber(trimToNull(request.tableNumber()));
		order.setCustomerName(customer == null ? trimToNull(request.customerName()) : customer.getFullName());
		order.setCustomerPhone(customer == null ? trimToNull(request.customerPhone()) : customer.getPhone());
		order.setNotes(trimToNull(request.notes()));

		List<OrderLine> lines = new ArrayList<>();
		BigDecimal total = BigDecimal.ZERO;
		for (OrderLineRequest lineRequest : request.items()) {
			OrderLine line = buildLine(lineRequest);
			lines.add(line);
			total = total.add(line.getLineTotal());
		}
		order.replaceLines(lines);
		order.setTotalAmount(normalizeAmount(total));
		return toResponse(orderRepository.save(order));
	}

	@Transactional(readOnly = true)
	public List<OrderResponse> list() {
		return orderRepository.findAllByOrderByCreatedAtDesc().stream()
				.map(this::toResponse)
				.toList();
	}

	@Transactional(readOnly = true)
	public OrderResponse get(Long id) {
		return toResponse(findOrder(id));
	}

	@Transactional
	public OrderResponse accept(Long id, String username) {
		RestaurantOrder order = findOrder(id);
		if (order.getStatus() != OrderStatus.NEW) {
			throw new BusinessRuleException("Only NEW orders can be accepted.");
		}
		order.setStatus(OrderStatus.ACCEPTED);
		order.setAcceptedAt(Instant.now());
		order.setAcceptedBy(findUser(username));
		return toResponse(order);
	}

	private OrderLine buildLine(OrderLineRequest request) {
		MenuItem menuItem = menuItemRepository.findById(request.menuItemId())
				.orElseThrow(() -> new ResourceNotFoundException("Menu item not found: " + request.menuItemId()));
		if (!menuItem.isActive()) {
			throw new BusinessRuleException("Menu item is inactive: " + menuItem.getName());
		}

		PriceCycle cycle = request.priceCycle() == null ? PriceCycle.DAILY : request.priceCycle();
		ItemPrice price = menuItem.findPrice(cycle)
				.orElseThrow(() -> new BusinessRuleException(
						"No " + cycle + " price configured for menu item: " + menuItem.getName()));

		OrderLine line = new OrderLine();
		line.setMenuItem(menuItem);
		line.setPriceCycle(cycle);
		line.setQuantity(request.quantity());
		line.setUnitPrice(normalizeAmount(price.getAmount()));
		line.setLineTotal(normalizeAmount(price.getAmount().multiply(BigDecimal.valueOf(request.quantity()))));
		return line;
	}

	private RestaurantOrder findOrder(Long id) {
		return orderRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found: " + id));
	}

	private AppUser findUser(String username) {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
	}

	private OrderResponse toResponse(RestaurantOrder order) {
		List<OrderLineResponse> items = order.getLines().stream()
				.sorted(Comparator.comparing(line -> line.getMenuItem().getName()))
				.map(this::toResponse)
				.toList();
		return new OrderResponse(
				order.getId(),
				order.getOrderNumber(),
				order.getStatus(),
				order.getCustomer() == null ? null : order.getCustomer().getId(),
				order.getCustomerName(),
				order.getCustomerPhone(),
				order.getTableNumber(),
				order.getNotes(),
				normalizeAmount(order.getTotalAmount()),
				order.getCreatedAt(),
				order.getAcceptedAt(),
				order.getCreatedBy() == null ? null : order.getCreatedBy().getUsername(),
				order.getAcceptedBy() == null ? null : order.getAcceptedBy().getUsername(),
				items);
	}

	private OrderLineResponse toResponse(OrderLine line) {
		return new OrderLineResponse(
				line.getMenuItem().getId(),
				line.getMenuItem().getName(),
				line.getPriceCycle(),
				line.getQuantity(),
				normalizeAmount(line.getUnitPrice()),
				normalizeAmount(line.getLineTotal()));
	}

	private BigDecimal normalizeAmount(BigDecimal amount) {
		return amount.setScale(2, RoundingMode.HALF_UP);
	}

	private String trimToNull(String value) {
		if (value == null) {
			return null;
		}
		String trimmed = value.trim();
		return trimmed.isEmpty() ? null : trimmed;
	}

	private String generateOrderNumber() {
		return "ORD-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
	}
}
