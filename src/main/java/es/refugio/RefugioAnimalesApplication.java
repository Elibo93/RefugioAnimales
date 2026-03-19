package es.refugio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "es.refugio")
public class RefugioAnimalesApplication {

	public static void main(String[] args) {
		SpringApplication.run(RefugioAnimalesApplication.class, args);
	}

}
