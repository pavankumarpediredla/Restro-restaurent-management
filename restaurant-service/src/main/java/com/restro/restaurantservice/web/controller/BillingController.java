package com.restro.restaurantservice.web.controller;

import com.restro.restaurantservice.service.BillingService;
import com.restro.restaurantservice.web.dto.InvoiceCreateRequest;
import com.restro.restaurantservice.web.dto.InvoiceResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/billing/invoices")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class BillingController {

	private final BillingService billingService;

	public BillingController(BillingService billingService) {
		this.billingService = billingService;
	}

	@GetMapping
	public List<InvoiceResponse> list() {
		return billingService.list();
	}

	@GetMapping("/{id}")
	public InvoiceResponse get(@PathVariable Long id) {
		return billingService.get(id);
	}

	@PostMapping
	public ResponseEntity<InvoiceResponse> create(@Valid @RequestBody InvoiceCreateRequest request) {
		InvoiceResponse response = billingService.create(request);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}")
				.buildAndExpand(response.id())
				.toUri();
		return ResponseEntity.created(location).body(response);
	}

	@PatchMapping("/{id}/paid")
	public ResponseEntity<InvoiceResponse> markPaid(@PathVariable Long id) {
		return ResponseEntity.status(HttpStatus.OK).body(billingService.markPaid(id));
	}
}
