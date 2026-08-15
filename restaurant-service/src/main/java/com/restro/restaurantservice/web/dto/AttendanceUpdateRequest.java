package com.restro.restaurantservice.web.dto;

import com.restro.restaurantservice.domain.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

public record AttendanceUpdateRequest(@NotNull AttendanceStatus status, String note) {
}
