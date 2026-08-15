package com.restro.restaurantservice.web.controller;
import com.restro.restaurantservice.service.TableService; import com.restro.restaurantservice.web.dto.*; import jakarta.validation.Valid; import java.util.*; import org.springframework.http.*; import org.springframework.security.core.Authentication; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/tables") public class TableController {
 private final TableService service; public TableController(TableService service){this.service=service;}
 @GetMapping @PreAuthorize("hasAnyRole('OWNER','ADMIN','MANAGER','WAITER','CASHIER')") public List<TableResponse> list(@RequestParam(required=false) Integer capacity){return service.list(capacity);}
 @GetMapping("/{id}") @PreAuthorize("hasAnyRole('OWNER','ADMIN','MANAGER','WAITER','CASHIER')") public TableResponse get(@PathVariable Long id){return service.get(id);}
 @PostMapping @PreAuthorize("hasAnyRole('OWNER','ADMIN','MANAGER')") public ResponseEntity<TableResponse> create(@Valid @RequestBody TableRequest r){return ResponseEntity.status(HttpStatus.CREATED).body(service.create(r));}
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('OWNER','ADMIN','MANAGER')") public TableResponse update(@PathVariable Long id,@Valid @RequestBody TableRequest r){return service.update(id,r);}
 @PostMapping("/{id}/hold") @PreAuthorize("hasAnyRole('OWNER','ADMIN','MANAGER','WAITER','CASHIER')") public TableResponse hold(@PathVariable Long id,@Valid @RequestBody HoldTableRequest r,Authentication a){return service.hold(id,r,a.getName());}
 @PostMapping("/{id}/release") @PreAuthorize("hasAnyRole('OWNER','ADMIN','MANAGER','WAITER','CASHIER')") public TableResponse release(@PathVariable Long id,Authentication a){return service.release(id,a.getName());}
}
