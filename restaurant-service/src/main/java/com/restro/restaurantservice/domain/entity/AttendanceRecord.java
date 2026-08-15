package com.restro.restaurantservice.domain.entity;

import com.restro.restaurantservice.domain.enums.AttendanceStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "attendance_records", uniqueConstraints = @UniqueConstraint(
		name = "uk_attendance_staff_date", columnNames = {"staff_id", "attendance_date"}))
public class AttendanceRecord {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "staff_id", nullable = false)
	private AppUser staff;

	@Column(name = "attendance_date", nullable = false)
	private LocalDate attendanceDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 24)
	private AttendanceStatus status;

	@Column(length = 500)
	private String note;

	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@Column(nullable = false)
	private Instant updatedAt;

	protected AttendanceRecord() {
	}

	public AttendanceRecord(AppUser staff, LocalDate attendanceDate, AttendanceStatus status, String note) {
		this.staff = staff;
		this.attendanceDate = attendanceDate;
		this.status = status;
		this.note = note;
	}

	@jakarta.persistence.PrePersist
	void onCreate() {
		createdAt = Instant.now();
		updatedAt = createdAt;
	}

	@jakarta.persistence.PreUpdate
	void onUpdate() {
		updatedAt = Instant.now();
	}

	public Long getId() { return id; }
	public AppUser getStaff() { return staff; }
	public LocalDate getAttendanceDate() { return attendanceDate; }
	public AttendanceStatus getStatus() { return status; }
	public String getNote() { return note; }
	public Instant getCreatedAt() { return createdAt; }
	public Instant getUpdatedAt() { return updatedAt; }
	public void setStatus(AttendanceStatus status) { this.status = status; }
	public void setNote(String note) { this.note = note; }
}
