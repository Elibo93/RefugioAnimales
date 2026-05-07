package es.refugio.auth.infrastructure.startup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import es.refugio.auth.domain.Rol;
import es.refugio.refugio.infraestructure.db.jpa.repository.usuario.UsuarioEntityJpaRepository;
import es.refugio.refugio.infraestructure.db.jpa.entity.UsuarioEntity;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UsuarioEntityJpaRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioRepository.findByEmail("admin@refugio.es").isEmpty()) {
            log.info("No se encontró el usuario administrador. Creando usuario administrador por defecto...");
            UsuarioEntity admin = UsuarioEntity.builder()
                    .email("admin@refugio.es")
                    .username("admin")
                    .contrasena(passwordEncoder.encode("admin123"))
                    .rol(Rol.ROLE_ADMIN)
                    .createdAt(LocalDateTime.now())
                    .build();
            usuarioRepository.save(admin);
            log.info("Usuario administrador por defecto creado: admin@refugio.es / admin123");
        }

        if (usuarioRepository.findByEmail("anonimo@refugio.es").isEmpty()) {
            log.info("Creando usuario anónimo para donaciones sin registro...");
            UsuarioEntity anonimo = UsuarioEntity.builder()
                    .email("anonimo@refugio.es")
                    .username("anonimo")
                    .contrasena(passwordEncoder.encode("anonimo123"))
                    .rol(Rol.ROLE_PUBLICO)
                    .createdAt(LocalDateTime.now())
                    .build();
            usuarioRepository.save(anonimo);
            log.info("Usuario anónimo creado: anonimo@refugio.es");
        }
    }
}
