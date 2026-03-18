package es.refugio.auth.infrastructure.startup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import es.refugio.auth.domain.Rol;
import es.refugio.auth.domain.UserEntity;
import es.refugio.auth.infrastructure.repository.UserRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            log.info("No se encontraron usuarios en la base de datos. Creando usuario administrador por defecto...");
            UserEntity admin = UserEntity.builder()
                    .email("admin@refugio.es")
                    .password(passwordEncoder.encode("admin123"))
                    .rol(Rol.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Usuario administrador por defecto creado: admin@refugio.es / admin123");
        }
    }
}
