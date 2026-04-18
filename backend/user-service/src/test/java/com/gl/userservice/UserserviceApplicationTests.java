package com.gl.userservice.service;

import com.gl.userservice.dto.AuthResponseDto;
import com.gl.userservice.dto.LoginRequestDto;
import com.gl.userservice.dto.RegisterRequestDto;
import com.gl.userservice.dto.UserResponseDto;
import com.gl.userservice.dto.WorkerApprovalRequestDto;
import com.gl.userservice.entity.Role;
import com.gl.userservice.entity.User;
import com.gl.userservice.exception.ResourceNotFoundException;
import com.gl.userservice.exception.UserAlreadyExistsException;
import com.gl.userservice.repository.UserRepository;
import com.gl.userservice.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private AuthenticationManager authenticationManager;

	@Mock
	private JwtService jwtService;

	@Mock
	private UserDetailsService userDetailsService;

	@InjectMocks
	private UserService userService;

	private RegisterRequestDto registerRequestDto;
	private LoginRequestDto loginRequestDto;
	private User user;

	@BeforeEach
	void setUp() {
		registerRequestDto = new RegisterRequestDto();
		registerRequestDto.setName("Rakshita");
		registerRequestDto.setEmail("rakshita@gmail.com");
		registerRequestDto.setPassword("12345");
		registerRequestDto.setPhone("9876543210");
		registerRequestDto.setAddress("Noida");
		registerRequestDto.setRole("USER");

		loginRequestDto = new LoginRequestDto();
		loginRequestDto.setEmail("rakshita@gmail.com");
		loginRequestDto.setPassword("12345");

		user = User.builder()
				.id(1L)
				.name("Rakshita")
				.email("rakshita@gmail.com")
				.password("encodedPassword")
				.phone("9876543210")
				.role(Role.USER)
				.address("Noida")
				.walletBalance(0.0)
				.verified(true)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();
	}

	@Test
	void register_shouldRegisterUserSuccessfully() {
		when(userRepository.existsByEmail(registerRequestDto.getEmail())).thenReturn(false);
		when(userRepository.existsByPhone(registerRequestDto.getPhone())).thenReturn(false);
		when(passwordEncoder.encode(registerRequestDto.getPassword())).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(user);

		String result = userService.register(registerRequestDto);

		assertEquals("User registered successfully", result);
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	void register_shouldThrowExceptionWhenEmailAlreadyExists() {
		when(userRepository.existsByEmail(registerRequestDto.getEmail())).thenReturn(true);

		assertThrows(UserAlreadyExistsException.class, () -> userService.register(registerRequestDto));
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void register_shouldThrowExceptionWhenPhoneAlreadyExists() {
		when(userRepository.existsByEmail(registerRequestDto.getEmail())).thenReturn(false);
		when(userRepository.existsByPhone(registerRequestDto.getPhone())).thenReturn(true);

		assertThrows(UserAlreadyExistsException.class, () -> userService.register(registerRequestDto));
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void register_shouldThrowExceptionWhenRoleIsAdmin() {
		registerRequestDto.setRole("ADMIN");

		when(userRepository.existsByEmail(registerRequestDto.getEmail())).thenReturn(false);
		when(userRepository.existsByPhone(registerRequestDto.getPhone())).thenReturn(false);

		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userService.register(registerRequestDto));

		assertEquals("ADMIN role cannot be registered via API", exception.getMessage());
		verify(userRepository, never()).save(any(User.class));
	}

	@Test
	void login_shouldReturnTokenWhenCredentialsAreValid() {
		UserDetails userDetails = org.springframework.security.core.userdetails.User
				.withUsername("rakshita@gmail.com")
				.password("encodedPassword")
				.roles("USER")
				.build();

		when(userDetailsService.loadUserByUsername(loginRequestDto.getEmail())).thenReturn(userDetails);
		when(jwtService.generateToken(userDetails)).thenReturn("mocked-jwt-token");

		AuthResponseDto response = userService.login(loginRequestDto);

		assertNotNull(response);
		assertEquals("mocked-jwt-token", response.getToken());
		assertEquals("Login successful", response.getMessage());

		verify(authenticationManager, times(1)).authenticate(
				any(UsernamePasswordAuthenticationToken.class)
		);
	}

	@Test
	void getProfile_shouldReturnUserProfile() {
		when(userRepository.findByEmail("rakshita@gmail.com")).thenReturn(Optional.of(user));

		UserResponseDto response = userService.getProfile("rakshita@gmail.com");

		assertNotNull(response);
		assertEquals(user.getEmail(), response.getEmail());
		assertEquals(user.getName(), response.getName());
		assertEquals(user.getPhone(), response.getPhone());
	}

	@Test
	void getProfile_shouldThrowExceptionWhenUserNotFound() {
		when(userRepository.findByEmail("rakshita@gmail.com")).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class,
				() -> userService.getProfile("rakshita@gmail.com"));
	}

	@Test
	void getPendingWorkers_shouldReturnPendingWorkers() {
		User worker = User.builder()
				.id(2L)
				.name("Sachin")
				.email("sachin@gmail.com")
				.password("encodedPassword")
				.phone("9876543211")
				.role(Role.WORKER)
				.address("Delhi")
				.walletBalance(0.0)
				.verified(false)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		when(userRepository.findByRoleAndVerified(Role.WORKER, false))
				.thenReturn(List.of(worker));

		List<UserResponseDto> result = userService.getPendingWorkers();

		assertEquals(1, result.size());
		assertEquals("Sachin", result.get(0).getName());
		assertEquals("WORKER", result.get(0).getRole());
	}

	@Test
	void approveWorker_shouldApproveWorkerSuccessfully() {
		WorkerApprovalRequestDto approvalRequestDto = new WorkerApprovalRequestDto();
		approvalRequestDto.setVerified(true);

		User worker = User.builder()
				.id(2L)
				.name("Sachin")
				.email("sachin@gmail.com")
				.password("encodedPassword")
				.phone("9876543211")
				.role(Role.WORKER)
				.address("Delhi")
				.walletBalance(0.0)
				.verified(false)
				.createdAt(LocalDateTime.now())
				.updatedAt(LocalDateTime.now())
				.build();

		when(userRepository.findById(2L)).thenReturn(Optional.of(worker));
		when(userRepository.save(any(User.class))).thenReturn(worker);

		UserResponseDto response = userService.approveWorker(2L, approvalRequestDto);

		assertNotNull(response);
		assertTrue(response.getVerified());
		verify(userRepository, times(1)).save(worker);
	}

	@Test
	void approveWorker_shouldThrowExceptionWhenUserIsNotWorker() {
		WorkerApprovalRequestDto approvalRequestDto = new WorkerApprovalRequestDto();
		approvalRequestDto.setVerified(true);

		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		RuntimeException exception = assertThrows(RuntimeException.class,
				() -> userService.approveWorker(1L, approvalRequestDto));

		assertEquals("User is not a worker", exception.getMessage());
	}

	@Test
	void approveWorker_shouldThrowExceptionWhenWorkerNotFound() {
		WorkerApprovalRequestDto approvalRequestDto = new WorkerApprovalRequestDto();
		approvalRequestDto.setVerified(true);

		when(userRepository.findById(100L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class,
				() -> userService.approveWorker(100L, approvalRequestDto));
	}
}
