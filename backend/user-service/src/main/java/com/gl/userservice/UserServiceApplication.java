package com.gl.userservice;

import com.gl.userservice.entity.Role;
import com.gl.userservice.entity.User;
import com.gl.userservice.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableFeignClients
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Bean
	CommandLineRunner initAdmin(UserRepository repo, PasswordEncoder encoder) {
		return args -> {
			if (repo.findByEmail("admin@gmail.com").isEmpty()) {

				User admin = User.builder()
						.name("Admin")
						.email("admin@gmail.com")
						.password(encoder.encode("Admin@123"))
						.phone("9999999998")
						.role(Role.ADMIN)
						.address("Bhopal")
						.walletBalance(0.0)
						.verified(true)
						.build();

				repo.save(admin);

				System.out.println("🔥 Default ADMIN created");
			}
		};
	}
}