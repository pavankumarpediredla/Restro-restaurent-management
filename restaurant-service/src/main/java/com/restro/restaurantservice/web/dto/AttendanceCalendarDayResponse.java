package com.restro.restaurantservice.web.dto;

import java.time.LocalDate;

public record AttendanceCalendarDayResponse(LocalDate attendanceDate, long present, long absent, long leave) {
}
