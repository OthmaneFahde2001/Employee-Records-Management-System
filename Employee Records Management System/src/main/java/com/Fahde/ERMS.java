package com.Fahde;

import com.Fahde.auth.Service.AuthenticationService;
import com.Fahde.auth.DTO.RegisterRequest;
import com.Fahde.auth.Entity.Role;
import com.Fahde.config.JwtService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;


@SpringBootApplication
public class ERMS {

	public static void main(String[] args) {
		SpringApplication.run(ERMS.class, args);
	}

	@Bean
	@Profile("!test")
	public CommandLineRunner commandLineRunner(
			AuthenticationService service, JwtService jwtService
			) {
		return args -> {
			var admin = RegisterRequest.builder()
					.userName("admin")
					.password("password")
					.role(String.valueOf(Role.ADMINISTRATOR))
					.build();
			String token=service.register(admin).getAccessToken();
			System.out.println("Admin token: " + token);


			var manager = RegisterRequest.builder()
					.userName("manager")
					.password("password")
					.role(String.valueOf(Role.MANAGER))
					.department("SI")
					.build();
			System.out.println("Manager token: " + service.register(manager).getAccessToken());

			var RH = RegisterRequest.builder()
					.userName("RH")
					.password("password")
					.role(String.valueOf(Role.HR_PERSONNEL))
					.build();
			System.out.println("RH token: " + service.register(RH).getAccessToken());

			System.out.println("ID: " + jwtService.extractId(token));
			System.out.println("role: " + jwtService.extractRole(token));
			System.out.println("username : " + jwtService.extractUsername(token));


		};
	}
}
