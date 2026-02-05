package br.gov.sus.sus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SusApplication {

	public static void main(String[] args) {
		SpringApplication.run(SusApplication.class, args);
	}

}
