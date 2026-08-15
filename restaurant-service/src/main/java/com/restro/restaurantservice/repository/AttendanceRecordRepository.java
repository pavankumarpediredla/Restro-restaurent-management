package com.restro.restaurantservice.repository;

import com.restro.restaurantservice.domain.entity.AttendanceRecord;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {
	List<AttendanceRecord> findByAttendanceDateOrderByStaff_DisplayNameAsc(LocalDate attendanceDate);
	List<AttendanceRecord> findByAttendanceDateBetweenOrderByAttendanceDateAsc(LocalDate from, LocalDate to);
	boolean existsByStaff_IdAndAttendanceDate(Long staffId, LocalDate attendanceDate);
	Optional<AttendanceRecord> findByStaff_IdAndAttendanceDate(Long staffId, LocalDate attendanceDate);
}
