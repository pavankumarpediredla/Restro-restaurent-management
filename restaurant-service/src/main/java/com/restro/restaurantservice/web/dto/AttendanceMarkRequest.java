package com.restro.restaurantservice.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public record AttendanceMarkRequest(
		@NotNull LocalDate attendanceDate,
		@NotEmpty List<@Valid AttendanceRecordRequest> records) {
}
