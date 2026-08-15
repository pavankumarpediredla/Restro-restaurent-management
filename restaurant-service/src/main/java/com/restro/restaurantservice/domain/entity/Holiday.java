package com.restro.restaurantservice.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "holidays", uniqueConstraints = @UniqueConstraint(name = "uk_holiday_date", columnNames = "holiday_date"))
public class Holiday {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
  @Column(name = "holiday_date", nullable = false) private LocalDate holidayDate;
  @Column(nullable = false, length = 120) private String name;
  @Column(length = 500) private String description;
  protected Holiday() {}
  public Holiday(LocalDate holidayDate, String name, String description) { this.holidayDate = holidayDate; this.name = name; this.description = description; }
  public Long getId() { return id; } public LocalDate getHolidayDate() { return holidayDate; } public String getName() { return name; } public String getDescription() { return description; }
  public void update(String name, String description) { this.name = name; this.description = description; }
}
