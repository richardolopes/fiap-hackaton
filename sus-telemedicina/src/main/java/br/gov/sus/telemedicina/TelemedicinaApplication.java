package br.gov.sus.telemedicina;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TelemedicinaApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelemedicinaApplication.class, args);
    }
}

