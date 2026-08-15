package com.restro.restaurantservice.web.dto;
import jakarta.validation.constraints.*; import java.time.Instant;
public record ReservationRequest(@NotNull Long tableId,@NotBlank String customerName,String phone,@NotNull Instant startsAt,@NotNull Instant endsAt,@Min(1) int guestCount,String notes) {}
