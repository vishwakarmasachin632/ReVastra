package com.gl.upcycleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class UpcycleserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UpcycleserviceApplication.class, args);
	}

}
