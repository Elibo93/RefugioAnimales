package es.refugio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "es.refugio")
@EnableDiscoveryClient
@EnableCaching
public class RefugioAnimalesApplication {

	public static void main(String[] args) {
		SpringApplication.run(RefugioAnimalesApplication.class, args);
	}

}
