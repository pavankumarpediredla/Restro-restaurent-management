package com.restro.restaurantservice.web.dto;

import java.time.LocalDate;
import java.util.List;

public record AttendanceDayResponse(LocalDate attendanceDate, List<AttendanceRecordResponse> records) {
}
