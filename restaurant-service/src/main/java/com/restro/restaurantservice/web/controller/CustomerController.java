package com.restro.restaurantservice.web.controller;

import com.restro.restaurantservice.service.CustomerService;
import com.restro.restaurantservice.web.dto.CustomerRequest;
import com.restro.restaurantservice.web.dto.CustomerResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/customers")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class CustomerController {

	private final CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	@GetMapping
	public List<CustomerResponse> list() {
		return customerService.list();
	}

	@GetMapping("/{id}")
	public CustomerResponse get(@PathVariable Long id) {
		return customerService.get(id);
	}

	@PostMapping
	public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
		CustomerResponse response = customerService.create(request);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(response.id())
				.toUri();
		return ResponseEntity.created(location).body(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<CustomerResponse> update(@PathVariable Long id, @Valid @RequestBody CustomerRequest request) {
		return ResponseEntity.status(HttpStatus.OK).body(customerService.update(id, request));
	}
}
