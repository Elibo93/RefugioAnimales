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

    private static final String DEFAULT_SEED_PASSWORD = "password";

    // Emails exactos del data.sql — solo estos usuarios se gestionan aquí
    private static final Set<String> SEED_EMAILS = Set.of(
        "laura.garcia@refugio.local",
        "carlos.martin@refugio.local",
        "marta.lopez@refugio.local",
        "diego.romero@local",
        "lucia.martinez@local",
        "mario.gomez@local",
        "sara.nadal@local",
        "pablo.diaz@local",
        "david.torres@local"
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
            boolean esValida = contrasena != null
                    && passwordEncoder.matches(DEFAULT_SEED_PASSWORD, contrasena);

            if (!esValida) {
                usuario.setContrasena(passwordEncoder.encode(DEFAULT_SEED_PASSWORD));
                usuarioRepository.save(usuario);
                actualizados++;
                log.info("[SeedPasswordInitializer] Contraseña recodificada para: {}", usuario.getEmail());
            }
        }

        if (actualizados > 0) {
            log.info("[SeedPasswordInitializer] {} contraseñas recodificadas correctamente.", actualizados);
        } else {
            log.info("[SeedPasswordInitializer] Todas las contraseñas de seed ya son correctas.");
        }
    }
}
