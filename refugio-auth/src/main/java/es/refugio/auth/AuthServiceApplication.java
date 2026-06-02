package es.refugio.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"es.refugio.auth", "es.refugio.refugio"})
@EnableDiscoveryClient
@EntityScan(basePackages = {"es.refugio.auth.domain", "es.refugio.refugio.infraestructure.db.jpa.entity"})
@EnableJpaRepositories(basePackages = {"es.refugio.auth.infrastructure.repository", "es.refugio.refugio.infraestructure.db.jpa.repository"})
public class AuthServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }
}
