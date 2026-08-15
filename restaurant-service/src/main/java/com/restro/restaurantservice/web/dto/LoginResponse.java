package com.restro.restaurantservice.web.dto;

public record LoginResponse(
		String token,
		UserResponse user) {
}
