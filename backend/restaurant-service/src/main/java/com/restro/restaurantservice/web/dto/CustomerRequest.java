package com.restro.restaurantservice.web.dto;

import jakarta.validation.constraints.NotBlank;

public record CustomerRequest(
		@NotBlank String fullName,
		String phone,
		String email,
		String address,
		Boolean active) {
}
