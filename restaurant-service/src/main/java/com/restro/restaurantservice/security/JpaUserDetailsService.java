package com.restro.restaurantservice.security;

import com.restro.restaurantservice.domain.entity.AppUser;
import com.restro.restaurantservice.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {

	private final AppUserRepository userRepository;

	public JpaUserDetailsService(AppUserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AppUser user = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Unknown user: " + username));

		return User.withUsername(user.getUsername())
				.password(user.getPassword())
				.disabled(!user.isEnabled())
				.roles(user.getRole().name())
				.build();
	}
}
