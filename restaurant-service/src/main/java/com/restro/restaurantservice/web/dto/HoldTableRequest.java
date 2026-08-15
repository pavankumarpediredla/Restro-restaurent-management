package com.restro.restaurantservice.web.dto;
import jakarta.validation.constraints.*;
public record HoldTableRequest(@Min(1) @Max(240) Integer minutes, String reason) {}
