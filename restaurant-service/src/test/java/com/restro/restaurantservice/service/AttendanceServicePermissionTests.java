package com.restro.restaurantservice.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.restro.restaurantservice.domain.entity.AppUser;
import com.restro.restaurantservice.domain.enums.UserRole;
import com.restro.restaurantservice.domain.exception.BusinessRuleException;
import com.restro.restaurantservice.repository.AppUserRepository;
import com.restro.restaurantservice.repository.AttendanceRecordRepository;
import com.restro.restaurantservice.repository.HolidayRepository;
import com.restro.restaurantservice.repository.WeekOffRepository;
import com.restro.restaurantservice.web.dto.AttendanceMarkRequest;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

class AttendanceServicePermissionTests {
	private final AppUserRepository users = Mockito.mock(AppUserRepository.class);
	private final AttendanceService service = new AttendanceService(Mockito.mock(AttendanceRecordRepository.class), users, Mockito.mock(HolidayRepository.class), Mockito.mock(WeekOffRepository.class));

	@AfterEach void clearSecurity() { SecurityContextHolder.clearContext(); }

	@Test void managerCannotMarkHistoricalAttendance() {
		setActor("manager"); when(users.findByUsername("manager")).thenReturn(Optional.of(user("manager", UserRole.MANAGER)));
		assertThrows(BusinessRuleException.class, () -> service.mark(new AttendanceMarkRequest(LocalDate.now().minusDays(1), List.of())));
	}

	@Test void ownerCanMarkHistoricalAttendance() {
		setActor("owner"); when(users.findByUsername("owner")).thenReturn(Optional.of(user("owner", UserRole.OWNER)));
		assertDoesNotThrow(() -> service.mark(new AttendanceMarkRequest(LocalDate.now().minusDays(1), List.of())));
	}

	private AppUser user(String username, UserRole role) { return new AppUser(username, "password", username, role); }
	private void setActor(String username) { SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(username, "password")); }
}
