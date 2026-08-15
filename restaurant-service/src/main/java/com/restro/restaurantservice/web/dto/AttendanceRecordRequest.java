package com.restro.restaurantservice.web.dto;

import com.restro.restaurantservice.domain.enums.AttendanceStatus;
import jakarta.validation.constraints.NotNull;

public record AttendanceRecordRequest(@NotNull Long staffId, @NotNull AttendanceStatus status, String note) {
}
