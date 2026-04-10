package es.refugio.frontend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RefugioFrontendApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefugioFrontendApplication.class, args);
    }
}
