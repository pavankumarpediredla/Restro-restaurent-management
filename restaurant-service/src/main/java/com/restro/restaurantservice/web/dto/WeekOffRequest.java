package com.restro.restaurantservice.web.dto;
import jakarta.validation.constraints.NotEmpty; import jakarta.validation.constraints.NotNull; import java.time.DayOfWeek; import java.util.Set;
public record WeekOffRequest(@NotNull Long staffId, @NotEmpty Set<DayOfWeek> days) {}
