package com.restro.restaurantservice.web.dto;
import java.time.DayOfWeek; import java.util.Set;
public record WeekOffResponse(Long staffId, Set<DayOfWeek> days) {}
