package com.restro.restaurantservice.domain.entity;

import jakarta.persistence.*;
import java.time.DayOfWeek;

@Entity
@Table(name = "week_offs", uniqueConstraints = @UniqueConstraint(name = "uk_week_off_staff_day", columnNames = {"staff_id", "week_day"}))
public class WeekOff {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @ManyToOne(optional = false) @JoinColumn(name = "staff_id", nullable = false) private AppUser staff;
  @Enumerated(EnumType.STRING) @Column(name = "week_day", nullable = false, length = 12) private DayOfWeek weekDay;
  protected WeekOff() {}
  public WeekOff(AppUser staff, DayOfWeek weekDay) { this.staff = staff; this.weekDay = weekDay; }
  public Long getId() { return id; } public AppUser getStaff() { return staff; } public DayOfWeek getWeekDay() { return weekDay; }
}
