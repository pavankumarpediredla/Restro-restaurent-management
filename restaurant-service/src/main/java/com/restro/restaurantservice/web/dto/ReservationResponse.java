package com.restro.restaurantservice.web.dto;
import com.restro.restaurantservice.domain.enums.ReservationStatus; import java.time.Instant;
public record ReservationResponse(Long id,Long tableId,String tableNumber,String customerName,String phone,Instant startsAt,Instant endsAt,int guestCount,ReservationStatus status,String notes) {}
