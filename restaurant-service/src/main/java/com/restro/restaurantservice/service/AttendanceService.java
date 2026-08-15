package com.restro.restaurantservice.service;

import com.restro.restaurantservice.domain.entity.AppUser;
import com.restro.restaurantservice.domain.entity.AttendanceRecord;
import com.restro.restaurantservice.domain.entity.Holiday;
import com.restro.restaurantservice.domain.entity.WeekOff;
import com.restro.restaurantservice.domain.enums.AttendanceStatus;
import com.restro.restaurantservice.domain.enums.UserRole;
import com.restro.restaurantservice.domain.exception.BusinessRuleException;
import com.restro.restaurantservice.domain.exception.ResourceNotFoundException;
import com.restro.restaurantservice.repository.AppUserRepository;
import com.restro.restaurantservice.repository.AttendanceRecordRepository;
import com.restro.restaurantservice.repository.HolidayRepository;
import com.restro.restaurantservice.repository.WeekOffRepository;
import com.restro.restaurantservice.web.dto.AttendanceCalendarDayResponse;
import com.restro.restaurantservice.web.dto.AttendanceDayResponse;
import com.restro.restaurantservice.web.dto.AttendanceMarkRequest;
import com.restro.restaurantservice.web.dto.AttendanceRecordRequest;
import com.restro.restaurantservice.web.dto.AttendanceRecordResponse;
import com.restro.restaurantservice.web.dto.AttendanceUpdateRequest;
import com.restro.restaurantservice.web.dto.AttendanceReportResponse;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

@Service
public class AttendanceService {
	private final AttendanceRecordRepository attendanceRepository;
	private final AppUserRepository userRepository;
	private final HolidayRepository holidayRepository;
	private final WeekOffRepository weekOffRepository;

	public AttendanceService(AttendanceRecordRepository attendanceRepository, AppUserRepository userRepository, HolidayRepository holidayRepository, WeekOffRepository weekOffRepository) {
		this.attendanceRepository = attendanceRepository;
		this.userRepository = userRepository;
		this.holidayRepository = holidayRepository;
		this.weekOffRepository = weekOffRepository;
	}

	@Transactional(readOnly = true)
	public List<AttendanceCalendarDayResponse> calendar(YearMonth month) {
		return attendanceRepository.findByAttendanceDateBetweenOrderByAttendanceDateAsc(month.atDay(1), month.atEndOfMonth()).stream()
				.collect(java.util.stream.Collectors.groupingBy(AttendanceRecord::getAttendanceDate))
				.entrySet().stream()
				.map(entry -> new AttendanceCalendarDayResponse(entry.getKey(),
						entry.getValue().stream().filter(record -> record.getStatus().name().equals("PRESENT")).count(),
						entry.getValue().stream().filter(record -> record.getStatus().name().equals("ABSENT")).count(),
						entry.getValue().stream().filter(record -> record.getStatus().name().equals("LEAVE")).count()))
				.toList();
	}

	@Transactional(readOnly = true)
	public AttendanceDayResponse day(LocalDate date) {
		return new AttendanceDayResponse(date, attendanceRepository.findByAttendanceDateOrderByStaff_DisplayNameAsc(date).stream().map(this::map).toList());
	}

	@Transactional
	public List<AttendanceRecordResponse> mark(AttendanceMarkRequest request) {
		requireDatePermission(request.attendanceDate());
		Set<Long> staffIds = new HashSet<>();
		for (AttendanceRecordRequest record : request.records()) {
			if (!staffIds.add(record.staffId())) {
				throw new BusinessRuleException("A staff member can only be marked once per date");
			}
		}
		return request.records().stream().map(record -> {
			AppUser staff = requireEligibleStaff(record.staffId());
			AttendanceRecord existing = attendanceRepository.findByStaff_IdAndAttendanceDate(staff.getId(), request.attendanceDate()).orElse(null);
			if (existing != null) { existing.setStatus(record.status()); existing.setNote(cleanNote(record.note())); return map(existing); }
			return map(attendanceRepository.save(new AttendanceRecord(staff, request.attendanceDate(), record.status(), cleanNote(record.note()))));
		}).toList();
	}

	@Transactional
	public AttendanceRecordResponse update(Long id, AttendanceUpdateRequest request) {
		AttendanceRecord record = attendanceRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Attendance record not found: " + id));
		requireDatePermission(record.getAttendanceDate());
		requireEligibleStaff(record.getStaff().getId());
		record.setStatus(request.status());
		record.setNote(cleanNote(request.note()));
		return map(record);
	}

	@Transactional
	public void delete(Long id) { attendanceRepository.delete(attendanceRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Attendance record not found: " + id))); }

	@Transactional(readOnly = true)
	public AttendanceReportResponse report(YearMonth month) {
		List<AttendanceRecordResponse> actual = attendanceRepository.findByAttendanceDateBetweenOrderByAttendanceDateAsc(month.atDay(1), month.atEndOfMonth()).stream().map(this::map).toList();
		List<AppUser> staff = userRepository.findAll().stream().filter(user -> isEligible(user.getRole())).toList();
		Set<String> recorded = actual.stream().map(item -> item.staffId() + ":" + item.attendanceDate()).collect(java.util.stream.Collectors.toSet());
		Set<LocalDate> holidays = holidayRepository.findByHolidayDateBetweenOrderByHolidayDate(month.atDay(1), month.atEndOfMonth()).stream().map(Holiday::getHolidayDate).collect(java.util.stream.Collectors.toSet());
		java.util.Map<Long, Set<java.time.DayOfWeek>> weekOffs = weekOffRepository.findByStaff_IdIn(staff.stream().map(AppUser::getId).toList()).stream().collect(java.util.stream.Collectors.groupingBy(item -> item.getStaff().getId(), java.util.stream.Collectors.mapping(WeekOff::getWeekDay, java.util.stream.Collectors.toSet())));
		List<AttendanceRecordResponse> generated = new java.util.ArrayList<>();
		for (AppUser user : staff) for (LocalDate date = month.atDay(1); !date.isAfter(month.atEndOfMonth()); date = date.plusDays(1)) {
			if (recorded.contains(user.getId() + ":" + date)) continue;
			AttendanceStatus status = holidays.contains(date) ? AttendanceStatus.HOLIDAY : weekOffs.getOrDefault(user.getId(), Set.of()).contains(date.getDayOfWeek()) ? AttendanceStatus.WEEK_OFF : null;
			if (status != null) generated.add(new AttendanceRecordResponse(null, user.getId(), user.getDisplayName(), user.getUsername(), user.getRole(), user.isEnabled(), date, status, null));
		}
		List<AttendanceRecordResponse> all = new java.util.ArrayList<>(actual); all.addAll(generated); return new AttendanceReportResponse(all);
	}

	@Transactional(readOnly = true)
	public AttendanceReportResponse staffReport(Long staffId, YearMonth month) {
		AppUser actor = currentActor();
		if (actor.getRole() != UserRole.OWNER && actor.getRole() != UserRole.MANAGER && !actor.getId().equals(staffId)) {
			throw new BusinessRuleException("Staff can only view their own attendance.");
		}
		return new AttendanceReportResponse(report(month).records().stream().filter(record -> record.staffId().equals(staffId)).toList());
	}

	private void requireDatePermission(LocalDate date) {
		AppUser actor = currentActor();
		if (actor.getRole() == UserRole.MANAGER && !date.equals(LocalDate.now(ZoneId.systemDefault()))) throw new BusinessRuleException("Managers can only modify attendance for the current date.");
	}
	private AppUser currentActor() { return userRepository.findByUsername(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow(() -> new ResourceNotFoundException("Current user not found")); }

	@Transactional(readOnly = true)
	public List<AttendanceRecordResponse> eligibleStaff() {
		return userRepository.findAll(Sort.by(Sort.Direction.ASC, "displayName")).stream()
				.filter(user -> isEligible(user.getRole()))
				.map(user -> new AttendanceRecordResponse(null, user.getId(), user.getDisplayName(), user.getUsername(), user.getRole(), user.isEnabled(), null, null, null))
				.toList();
	}

	private AppUser requireEligibleStaff(Long id) {
		AppUser user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Staff member not found: " + id));
		if (!isEligible(user.getRole())) {
			throw new BusinessRuleException("Attendance cannot be marked for an owner or administrator");
		}
		return user;
	}

	private boolean isEligible(UserRole role) { return role != UserRole.OWNER && role != UserRole.ADMIN; }
	private String cleanNote(String note) { return note == null || note.isBlank() ? null : note.trim(); }
	private AttendanceRecordResponse map(AttendanceRecord record) {
		AppUser staff = record.getStaff();
		return new AttendanceRecordResponse(record.getId(), staff.getId(), staff.getDisplayName(), staff.getUsername(), staff.getRole(), staff.isEnabled(), record.getAttendanceDate(), record.getStatus(), record.getNote());
	}
}
