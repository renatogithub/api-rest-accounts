package com.nttdata.apirestaccounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ApiRestAccountsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiRestAccountsApplication.class, args);
	}

}
