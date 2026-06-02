package es.refugio.auth.infrastructure.startup;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import es.refugio.refugio.infraestructure.db.jpa.entity.UsuarioEntity;
import es.refugio.refugio.infraestructure.db.jpa.repository.usuario.UsuarioEntityJpaRepository;

import java.util.List;
import java.util.Set;

/**
 * Inicializador que garantiza que los usuarios del seed (data.sql) tienen
 * contraseñas codificadas con BCrypt correctamente.
 *
 * IMPORTANTE: Solo actúa sobre los usuarios del seed (emails definidos en data.sql).
 * Los usuarios registrados manualmente tienen sus propias contraseñas y NO deben
 * ser modificados por este inicializador.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Order(2)
public class SeedPasswordInitializer implements CommandLineRunner {

    private static final String DEFAULT_SEED_PASSWORD = "password123";

    // Emails exactos del data.sql — solo estos usuarios se gestionan aquí
    private static final Set<String> SEED_EMAILS = Set.of(
        "laura@mail.com",
        "carlos@mail.com",
        "marta@mail.com",
        "diego@mail.com",
        "lucia@mail.com",
        "mario@mail.com",
        "sara@mail.com",
        "pablo@mail.com",
        "david@mail.com",
        "elena@mail.com",
        "javier@mail.com",
        "ana@mail.com",
        "sergio@mail.com",
        "clara@mail.com",
        "roberto@mail.com",
        "sofia@mail.com",
        "miguel@mail.com",
        "isabel@mail.com",
        "antonio@mail.com"
    );

    private final UsuarioEntityJpaRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        List<UsuarioEntity> usuarios = usuarioRepository.findAll();
        int actualizados = 0;

        for (UsuarioEntity usuario : usuarios) {
            // Solo procesamos los usuarios del seed definidos en data.sql
            if (!SEED_EMAILS.contains(usuario.getEmail())) {
                continue;
            }

            String contrasena = usuario.getContrasena();
            
            // Si la contraseña ya está encriptada y es "password123", no hacemos nada
            // Verificamos si empieza por $2a$ (hash BCrypt común) y si hace match
            boolean yaEstaCorrecta = contrasena != null 
                    && contrasena.startsWith("$2a$") 
                    && passwordEncoder.matches(DEFAULT_SEED_PASSWORD, contrasena);

            if (!yaEstaCorrecta) {
                usuario.setContrasena(passwordEncoder.encode(DEFAULT_SEED_PASSWORD));
                usuarioRepository.save(usuario);
                actualizados++;
                log.info("[SeedPasswordInitializer] Contraseña encriptada correctamente para: {}", usuario.getEmail());
            }
        }

        if (actualizados > 0) {
            log.info("[SeedPasswordInitializer] {} contraseñas recodificadas correctamente.", actualizados);
        } else {
            log.info("[SeedPasswordInitializer] Todas las contraseñas de seed ya son correctas.");
        }
    }
}
