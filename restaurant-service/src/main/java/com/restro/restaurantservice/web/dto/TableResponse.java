package com.restro.restaurantservice.web.dto;
import com.restro.restaurantservice.domain.enums.TableStatus; import java.time.Instant;
public record TableResponse(Long id,String tableNumber,String tableName,int capacity,String tableType,String section,TableStatus status,boolean active,String heldBy,Instant heldAt,Instant holdExpiresAt,String holdReason,Long activeOrderId,String activeOrderNumber,String activeOrderCustomer,Integer activeOrderItems,java.math.BigDecimal activeOrderTotal,Instant activeOrderStartedAt) {}
