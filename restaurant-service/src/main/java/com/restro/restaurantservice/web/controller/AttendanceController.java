package com.restro.restaurantservice.web.controller;

import com.restro.restaurantservice.service.AttendanceService;
import com.restro.restaurantservice.web.dto.AttendanceCalendarDayResponse;
import com.restro.restaurantservice.web.dto.AttendanceDayResponse;
import com.restro.restaurantservice.web.dto.AttendanceMarkRequest;
import com.restro.restaurantservice.web.dto.AttendanceRecordResponse;
import com.restro.restaurantservice.web.dto.AttendanceUpdateRequest;
import com.restro.restaurantservice.web.dto.AttendanceReportResponse;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/attendance")
@PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
public class AttendanceController {
	private final AttendanceService attendanceService;

	public AttendanceController(AttendanceService attendanceService) { this.attendanceService = attendanceService; }

	@GetMapping("/calendar")
	public List<AttendanceCalendarDayResponse> calendar(@RequestParam(name = "month") YearMonth month) { return attendanceService.calendar(month); }

	@GetMapping("/day")
	public AttendanceDayResponse day(@RequestParam(name = "date") LocalDate date) { return attendanceService.day(date); }

	@GetMapping("/staff")
	public List<AttendanceRecordResponse> staff() { return attendanceService.eligibleStaff(); }

	@GetMapping("/report")
	public AttendanceReportResponse report(@RequestParam(name = "month") YearMonth month) { return attendanceService.report(month); }

	@PostMapping
	public List<AttendanceRecordResponse> mark(@Valid @RequestBody AttendanceMarkRequest request) { return attendanceService.mark(request); }

	@PutMapping("/{id}")
	@PreAuthorize("hasAnyRole('OWNER', 'MANAGER')")
	public AttendanceRecordResponse update(@PathVariable Long id, @Valid @RequestBody AttendanceUpdateRequest request) {
		return attendanceService.update(id, request);
	}

	@DeleteMapping("/{id}") @PreAuthorize("hasRole('OWNER')") public void delete(@PathVariable Long id) { attendanceService.delete(id); }
}
