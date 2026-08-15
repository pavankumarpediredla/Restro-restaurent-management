package com.restro.restaurantservice.web.dto;

import com.restro.restaurantservice.domain.enums.AttendanceStatus;
import com.restro.restaurantservice.domain.enums.UserRole;
import java.time.LocalDate;

public record AttendanceRecordResponse(
		Long id,
		Long staffId,
		String staffName,
		String staffUsername,
		UserRole staffRole,
		boolean staffEnabled,
		LocalDate attendanceDate,
		AttendanceStatus status,
		String note) {
}
