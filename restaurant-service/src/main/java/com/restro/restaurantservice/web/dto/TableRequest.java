package com.restro.restaurantservice.web.dto;
import com.restro.restaurantservice.domain.enums.TableStatus; import jakarta.validation.constraints.*;
public record TableRequest(@NotBlank String tableNumber, String tableName, @Min(1) @Max(1000) int capacity, String tableType, String section, TableStatus status, Boolean active) {}
