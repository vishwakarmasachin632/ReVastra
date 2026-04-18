package com.gl.recyclingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RecyclingserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RecyclingserviceApplication.class, args);
	}

}
