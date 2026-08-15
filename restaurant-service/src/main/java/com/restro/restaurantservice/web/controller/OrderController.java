package com.restro.restaurantservice.web.controller;

import com.restro.restaurantservice.service.OrderService;
import com.restro.restaurantservice.web.dto.OrderCreateRequest;
import com.restro.restaurantservice.web.dto.OrderResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

	private final OrderService orderService;

	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@GetMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'KITCHEN')")
	public List<OrderResponse> list() {
		return orderService.list();
	}

	@GetMapping("/{id}")
	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'KITCHEN')")
	public OrderResponse get(@PathVariable Long id) {
		return orderService.get(id);
	}

	@PostMapping
	@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
	public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderCreateRequest request, Authentication authentication) {
		OrderResponse response = orderService.create(authentication.getName(), request);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(response.id())
				.toUri();
		return ResponseEntity.created(location).body(response);
	}

	@PostMapping("/{id}/accept")
	@PreAuthorize("hasRole('KITCHEN')")
	public ResponseEntity<OrderResponse> accept(@PathVariable Long id, Authentication authentication) {
		return ResponseEntity.status(HttpStatus.OK).body(orderService.accept(id, authentication.getName()));
	}
}
