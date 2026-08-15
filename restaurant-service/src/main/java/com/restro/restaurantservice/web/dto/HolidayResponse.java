package com.restro.restaurantservice.web.dto;
import java.time.LocalDate;
public record HolidayResponse(Long id, LocalDate holidayDate, String name, String description) {}
