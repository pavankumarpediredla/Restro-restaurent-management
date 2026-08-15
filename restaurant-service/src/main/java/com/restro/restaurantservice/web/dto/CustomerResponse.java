package com.restro.restaurantservice.web.dto;

import java.time.Instant;

public record CustomerResponse(
		Long id,
		String fullName,
		String phone,
		String email,
		String address,
		boolean active,
		Instant createdAt,
		Instant updatedAt) {
}
